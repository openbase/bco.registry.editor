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
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class SendableTreeItem<MB extends Message.Builder> extends BuilderTreeItem<MB> {

    private final SimpleObjectProperty<Boolean> changedProperty;
    private final FieldDescriptor idField, labelField;

    public SendableTreeItem(FieldDescriptor fieldDescriptor, MB builder) throws InitializationException {
        super(fieldDescriptor, builder);

        idField = ProtoBufFieldProcessor.getFieldDescriptor(builder, Identifiable.TYPE_FIELD_ID);
        labelField = ProtoBufFieldProcessor.getFieldDescriptor(builder, rst.configuration.LabelType.Label.class.getSimpleName().toLowerCase());

        this.changedProperty = new SimpleObjectProperty<>(false);

        this.addEventHandler(valueChangedEvent(), event -> {
            // this is triggered when the value of this node or one of its children changes
            changedProperty.set(true);
        });

//        this.addEventHandler(childrenModificationEvent(), event -> {
//            // this is triggered when children are removed or added
//            changedProperty.set(true);
//        });
    }

    @Override
    protected TreeItem<ValueType> createChild(final FieldDescriptor field) throws CouldNotPerformException {
        if (field.equals(idField)) {
            return new LeafTreeItem<>(field, getBuilder().getField(field), getBuilder(), false);
        }
        return super.createChild(field);
    }

    public SimpleObjectProperty<Boolean> getChangedProperty() {
        return changedProperty;
    }

    @Override
    public Node getDescriptionGraphic() {
        try {
            return new Label(LabelProcessor.getBestMatch((rst.configuration.LabelType.Label) getBuilder().getField(labelField)));
        } catch (NotAvailableException e) {
            return super.getDescriptionGraphic();
        }
    }

    @Override
    public Node getValueGraphic() {
        if (changedProperty.get()) {
            final Button applyButton, cancelButton;
            final HBox buttonLayout;
            applyButton = new Button("Apply");
            applyButton.setOnAction(event -> GlobalCachedExecutorService.submit(() -> {
                logger.info("Apply button pressed");
                if (!getBuilder().isInitialized()) {
                    if (ProtoBufFieldProcessor.checkIfSomeButNotAllRequiredFieldsAreSet(getBuilder())) {
                        new RequiredFieldAlert(getBuilder());
                        if (!getBuilder().isInitialized()) {
                            return null;
                        }
                    } else {
                        ProtoBufFieldProcessor.clearRequiredFields(getBuilder());
                    }
                }
                Future<Message> task = null;
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
                        task = Registries.update(message);
//                                    registryTask = Registries.update(msg);
                        final Message update = task.get();

                        // check if update and original are the same, then the changed values where reset and a registry update is not triggered
                        if (original.equals(update)) {
                            // reset the container to its old value
                            SendableTreeItem.this.update((MB) original.toBuilder());
                        }

                    } else {
                        task = Registries.register(message);
                        task.get();
                        // remove temporally created node structure
                        SendableTreeItem.this.getParent().getChildren().remove(SendableTreeItem.this);
                    }
                } catch (CouldNotPerformException | ExecutionException ex) {
                    RegistryEditor.printException(ex, logger, LogLevel.ERROR);
                    if (task != null && !task.isDone()) {
                        task.cancel(true);
                    }

                    SendableTreeItem.this.changedProperty.set(false);
                }
                return true;
            }));
            cancelButton = new Button("Cancel");
//            cancelButton.setOnAction(new CancelEventHandler());
            buttonLayout = new HBox(applyButton, cancelButton);
            return buttonLayout;
        }
        return super.getValueGraphic();
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
}
