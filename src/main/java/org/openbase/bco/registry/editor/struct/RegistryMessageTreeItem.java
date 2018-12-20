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
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import org.openbase.bco.registry.editor.RegistryEditorOld;
import org.openbase.bco.registry.editor.visual.RequiredFieldAlert;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.ExceptionProcessor;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.jul.iface.Identifiable;
import org.openbase.jul.schedule.GlobalCachedExecutorService;

import java.util.HashSet;
import java.util.Optional;
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
        try {
            idField = ProtoBufFieldProcessor.getFieldDescriptor(builder, Identifiable.TYPE_FIELD_ID);
            labelField = ProtoBufFieldProcessor.getFieldDescriptor(builder, rst.language.LabelType.Label.class.getSimpleName().toLowerCase());

            changed = false;
            inUpdate = false;

            this.addEventHandler(valueChangedEvent(), event -> {
                // this is triggered when the value of this node or one of its children changes
                updateDescriptionGraphic();

                if (!inUpdate && !(event.getSource().equals(RegistryMessageTreeItem.this))) {
                    logger.debug("Set changed");
                    changed = true;
                }

                updateValueGraphic();
            });
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    public String getId() {
        return (String) getBuilder().getField(idField);
    }

    /**
     * Match a builder by comparing their ids.
     *
     * @param builder {@inheritDoc}
     *
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
    protected String createDescriptionText() {
        try {
            return LabelProcessor.getBestMatch((rst.language.LabelType.Label) getBuilder().getField(labelField));
        } catch (NotAvailableException e) {
            return super.createDescriptionText();
        }
    }

    @Override
    protected Node createValueGraphic() {
        // task is not done yet so display loading graphic
        if (registryTask != null && !registryTask.isDone()) {
            final Label label = new Label("Waiting for registry update...");
            final ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setMaxHeight(16);

            final HBox hBox = new HBox();
            hBox.setSpacing(5);
            hBox.getChildren().addAll(progressIndicator, label);
            return hBox;
        }

        final Label errorLabel = new Label();
        // task is done but not reset meaning it failed, so generate an error label
        if (registryTask != null) {
            errorLabel.setStyle("-fx-text-background-color: rgb(255,0,0); -fx-font-weight: bold;");
            // done but failed
            try {
                registryTask.get();
            } catch (InterruptedException ex) {
                // this should not because the task should already be done
            } catch (ExecutionException ex) {
                try {
                    errorLabel.setText(ExceptionProcessor.getInitialCause(ex).getMessage());
                } catch (NotAvailableException exx) {
                    errorLabel.setText(exx.getMessage());
                }
            }
        }

        if (changed) {
            logger.debug("Create buttons");
            final Button applyButton, cancelButton;
            final HBox buttonLayout;
            applyButton = new Button("Apply");
            applyButton.setOnAction(event -> handleApplyEvent());
            cancelButton = new Button("Cancel");
            cancelButton.setOnAction(event -> handleCancelEvent());
            buttonLayout = new HBox(applyButton, cancelButton);
            if (registryTask != null) {
                // if task failed add error to button layout
                buttonLayout.getChildren().add(errorLabel);
            }
            return buttonLayout;
        }

        if (registryTask != null) {
            return errorLabel;
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
                ExceptionPrinter.printHistory("Could not retrieve message with id[" + id + "] for type[" + getBuilder().getClass().getName() + "] from registry", ex, logger, LogLevel.WARN);
            }
        }
    }

    private void handleApplyEvent() {
        logger.info("Apply button pressed");
        handleRequiredFields();

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
                changed = false;
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
                    } catch (InterruptedException e) {
                        // just let the thread finish
                    } catch (ExecutionException e) {
                        changed = true;
                        // update the current value which will trigger an update of the displayed graphics,
                        // this will display the exception to the user
                        setValue(getValueCasted().createNew(getBuilder()));
                    }
                });

            } else {
                registryTask = Registries.register(message);
                GlobalCachedExecutorService.submit(() -> {
                    try {
                        registryTask.get();
                        Platform.runLater(() -> getParent().getChildren().remove(this));
                    } catch (InterruptedException e) {
                        // just let the thread finish
                    } catch (ExecutionException e) {
                        // update the current value which will trigger an update of the displayed graphics,
                        // this will display the exception to the user
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

                final Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Remove Action");
                alert.setHeaderText("Attention! Irreversible Action!");
                alert.setContentText("Do you really want to remove " + getDescriptionText() + "?");

                final Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK) {
                    return;
                }

                // always remove by id to ignore not initialized fields of the builder
                registryTask = Registries.remove(Registries.getById(id, getBuilder()));
                setValue(getValueCasted().createNew(getBuilder()));
                GlobalCachedExecutorService.submit(() -> {
                    try {
                        registryTask.get();
                    } catch (InterruptedException e) {
                        // just let the thread finish
                    } catch (ExecutionException e) {
                        // update the current value which will trigger an update of the displayed graphics,
                        // this will display the exception to the user
                        setValue(getValueCasted().createNew(getBuilder()));
                    }
                });
            } else {
                Platform.runLater(() -> RegistryMessageTreeItem.this.getParent().getChildren().remove(RegistryMessageTreeItem.this));
            }
        } catch (CouldNotPerformException ex) {
            RegistryEditorOld.printException(ex, logger, LogLevel.WARN);
        }
    }

    public boolean isChanged() {
        return changed;
    }
}
