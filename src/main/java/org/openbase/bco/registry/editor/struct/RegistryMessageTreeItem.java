package org.openbase.bco.registry.editor.struct;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import org.openbase.bco.registry.editor.RegistryEditor;
import org.openbase.bco.registry.editor.visual.RequiredFieldAlert;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import org.openbase.jul.iface.Identifiable;
import org.openbase.jul.schedule.GlobalCachedExecutorService;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RegistryMessageTreeItem<MB extends Message.Builder> extends BuilderTreeItem<MB> {

    private final FieldDescriptor idField, labelField;

    private boolean inUpdate;
    private boolean changed;
    private Future<Message> registryTask;

    public RegistryMessageTreeItem(FieldDescriptor fieldDescriptor, MB builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        idField = ProtoBufFieldProcessor.getFieldDescriptor(builder, Identifiable.TYPE_FIELD_ID);
        labelField = ProtoBufFieldProcessor.getFieldDescriptor(builder, rst.configuration.LabelType.Label.class.getSimpleName().toLowerCase());

        changed = false;
        inUpdate = false;

        this.addEventHandler(valueChangedEvent(), event -> {
            // this is triggered when the value of this node or one of its children changes
            //TODO has to be handled if the parent is a list -> icon disappears
//            updateDescriptionGraphic();

            if (!inUpdate) {
                changed = true;
            }

            updateValueGraphic();
        });
    }

    /**
     * Match a builder by comparing their ids.
     *
     * @param builder {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    protected boolean matchesBuilder(final MB builder) {
        final Object id1 = getBuilder().getField(idField);
        final Object id2 = builder.getField(idField);
        return id1.equals(id2);
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        Set<Integer> uneditableFieldSet = new HashSet<>();
        uneditableFieldSet.add(idField.getNumber());
        return uneditableFieldSet;
    }

    @Override
    protected Node createDescriptionGraphic() {
        try {
            return new Label(LabelProcessor.getBestMatch((rst.configuration.LabelType.Label) getBuilder().getField(labelField)));
        } catch (NotAvailableException e) {
            return super.createDescriptionGraphic();
        }
    }

    @Override
    protected Node createValueGraphic() {
        if (registryTask != null && !registryTask.isDone()) {
            final Label label = new Label("Waiting for registry update...");
            final ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setMaxHeight(16);

            final HBox hBox = new HBox();
            hBox.setSpacing(5);
            hBox.getChildren().addAll(progressIndicator, label);
            return hBox;
        }

        if (changed) {
            logger.info("Create buttons");
            final Button applyButton, cancelButton;
            final HBox buttonLayout;
            applyButton = new Button("Apply");
            applyButton.setOnAction(event -> handleApplyEvent());
            cancelButton = new Button("Cancel");
            cancelButton.setOnAction(event -> handleCancelEvent());
            buttonLayout = new HBox(applyButton, cancelButton);
            return buttonLayout;
        }

        return super.createValueGraphic();
    }

    @Override
    public void update(MB value) throws CouldNotPerformException {
        //TODO handle local changes but global update

        inUpdate = true;
        try {
            changed = false;
            if (registryTask != null) {
                registryTask = null;
            }

            super.update(value);
        } finally {
            inUpdate = false;
        }
    }

    private void handleCancelEvent() {
        final String id = (String) getBuilder().getField(idField);
        if (id.isEmpty()) {
            getParent().getChildren().remove(RegistryMessageTreeItem.this);
        } else {
            try {
                final MB oldBuilder = (MB) Registries.getById(id, getBuilder()).toBuilder();
                try {
                    update(oldBuilder);
                } catch (CouldNotPerformException ex) {
                    logger.error("Could not update tree item with old builder from registry", ex);
                }
            } catch (CouldNotPerformException ex) {
                logger.warn("Could not retrieve message with id[" + id + "] for type[" + getBuilder().getClass().getName() + "] from registry", ex);
            }
        }
    }

    private void handleApplyEvent() {
        logger.info("Apply button pressed");
        handleRequiredFields();
        if (!getBuilder().isInitialized()) {
            return;
        }

        try {
            Message message;
            try {
                message = getBuilder().build();
            } catch (Throwable ex) {
                logger.info("Build failed", ex);
                throw ex;
            }
            if (Registries.contains(message)) {
                // save original value from model
                final Message original = Registries.getById(ProtoBufFieldProcessor.getId(message));
                registryTask = Registries.update(message);
                GlobalCachedExecutorService.submit(() -> {
                    final Message update;
                    try {
                        update = registryTask.get();

                        // check if update and original are the same, then the changed values where reset and a registry update is not triggered
                        if (original.equals(update)) {
                            // reset the container to its old value
                            Platform.runLater(() -> {
                                try {
                                    RegistryMessageTreeItem.this.update((MB) original.toBuilder());
                                } catch (CouldNotPerformException e) {
                                    logger.error("Could not reset tree item", e);
                                }
                            });
                        }
                        //TODO handle correctly
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        setValue(getValueCasted().createNew(getBuilder()));
                    }
                });

            } else {
                registryTask = Registries.register(message);
                GlobalCachedExecutorService.submit(() -> {
                    try {
                        registryTask.get();
                        //TODO handle correctly
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        setValue(getValueCasted().createNew(getBuilder()));
                    }

                });
            }
            setValue(getValueCasted().createNew(getBuilder()));
        } catch (CouldNotPerformException ex) {
            logger.error("Error while applying event");
        }
    }

    private void handleRequiredFields() {
        if (!getBuilder().isInitialized()) {
            if (ProtoBufFieldProcessor.checkIfSomeButNotAllRequiredFieldsAreSet(getBuilder())) {
                new RequiredFieldAlert(getBuilder());
            } else {
                ProtoBufFieldProcessor.clearRequiredFields(getBuilder());
            }
        }
    }

    void handleRemoveEvent() {
        final String id = (String) getBuilder().getField(idField);
        try {
            logger.debug("Removing message with Id [" + id + "]");
            if (!"".equals(id) && Registries.containsById(id, getBuilder())) {
                // always remove by id to ignore not initialized fields of the builder
                registryTask = Registries.remove(Registries.getById(id, getBuilder()));
                setValue(getValueCasted().createNew(getBuilder()));
                GlobalCachedExecutorService.submit(() -> {
                    try {
                        registryTask.get();
                        //TODO handle correctly
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                Platform.runLater(() -> RegistryMessageTreeItem.this.getParent().getChildren().remove(RegistryMessageTreeItem.this));
            }
        } catch (CouldNotPerformException ex) {
            RegistryEditor.printException(ex, logger, LogLevel.WARN);
        }
    }
}
