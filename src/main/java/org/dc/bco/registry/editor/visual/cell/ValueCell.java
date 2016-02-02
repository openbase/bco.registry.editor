package org.dc.bco.registry.editor.visual.cell;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 DivineCooperation
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

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Message;
import java.text.DecimalFormat;
import java.util.Date;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.dc.bco.registry.editor.RegistryEditor;
import org.dc.bco.registry.editor.struct.GenericGroupContainer;
import org.dc.bco.registry.editor.struct.GenericNodeContainer;
import org.dc.bco.registry.editor.struct.Leaf;
import org.dc.bco.registry.editor.struct.LeafContainer;
import org.dc.bco.registry.editor.struct.Node;
import org.dc.bco.registry.editor.struct.consistency.Configuration;
import org.dc.bco.registry.editor.util.FieldDescriptorUtil;
import org.dc.bco.registry.editor.util.SelectableLabel;
import org.dc.bco.registry.editor.visual.RegistryTreeTableView;
import org.dc.bco.registry.editor.visual.cell.editing.DecimalTextField;
import org.dc.bco.registry.editor.visual.cell.editing.EnumComboBox;
import org.dc.bco.registry.editor.visual.cell.editing.LongDatePicker;
import org.dc.bco.registry.editor.visual.cell.editing.MessageComboBox;
import org.dc.bco.registry.editor.visual.cell.editing.StringTextField;
import org.dc.bco.registry.editor.visual.cell.editing.UserConfigComboBoxConverter;
import org.dc.bco.registry.editor.visual.cell.editing.ValueCheckBox;
import org.dc.bco.registry.editor.visual.provider.DeviceClassItemDescriptorProvider;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.InstantiationException;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.extension.rsb.scope.ScopeGenerator;
import rst.authorization.UserGroupConfigType.UserGroupConfig;
import rst.configuration.EntryType;

/**
 *
 * @author thuxohl
 */
public class ValueCell extends RowCell {

    protected final Button applyButton, cancelButton;
    protected final HBox buttonLayout;
    protected LeafContainer leaf;

    protected SimpleObjectProperty<Boolean> changed = null;
    protected final ChangeListener<Boolean> changeListener;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public ValueCell() {
        super();
        applyButton = new Button("Apply");
        applyButton.setOnAction(new ApplyEventHandler());
        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(new CancelEventHandler());
        buttonLayout = new HBox(applyButton, cancelButton);
        this.changeListener = new ChangedListener();
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (getItem() instanceof Leaf && ((LeafContainer) getItem()).getEditable()) {
            leaf = ((LeafContainer) getItem());
            setGraphic(getEditingGraphic());
        }
    }

    private javafx.scene.control.Control getEditingGraphic() {

        javafx.scene.control.Control graphic = null;
        Message type = MessageComboBox.getMessageEnumBoxType(leaf.getDescriptor(), leaf.getParent().getBuilder());
        if (type != null) {
            try {
                graphic = new MessageComboBox(this, leaf.getParent().getBuilder(), leaf.getDescriptor());
            } catch (InstantiationException ex) {
                RegistryEditor.printException(ex, logger, LogLevel.ERROR);
            }
        } else if (leaf.getValue() instanceof String) {
            graphic = new StringTextField(this, (String) leaf.getValue());
        } else if (leaf.getValue() instanceof EnumValueDescriptor) {
            graphic = new EnumComboBox(this, (EnumValueDescriptor) leaf.getValue());
        } else if (leaf.getValue() instanceof Float || leaf.getValue() instanceof Double) {
            graphic = new DecimalTextField(this, leaf.getValue().toString());
        } else if (leaf.getValue() instanceof Long) {
            graphic = new LongDatePicker(this, (Long) leaf.getValue());
        } else if (leaf.getValue() instanceof Boolean) {
            graphic = new ValueCheckBox(this, true, false);
        }
        if (graphic != null) {
            graphic.setPrefWidth(this.getWidth() * 5 / 8);
        }
        return graphic;
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setGraphic(null);
    }

    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            setText("");
            setContextMenu(null);
        } else if (item instanceof Leaf) {
            String text = "";
            if (((Leaf) item).getValue() instanceof Long) {
                text = LongDatePicker.DATE_CONVERTER.format(new Date((Long) ((Leaf) item).getValue()));
            } else if (((Leaf) item).getValue() instanceof Double) {
                text = decimalFormat.format(((Double) ((Leaf) item).getValue()));
            } else if (((Leaf) item).getValue() instanceof EnumValueDescriptor) {
                text = (((EnumValueDescriptor) ((Leaf) item).getValue()).getName());
            } else if ((((Leaf) item).getValue() != null)) {
                if ("location_id".equals(item.getDescriptor()) || "parent_id".equals(item.getDescriptor()) || "child_id".equals(item.getDescriptor()) || "tile_id".equals(item.getDescriptor())) {
                    try {
                        text = ScopeGenerator.generateStringRep(remotePool.getLocationRemote().getLocationConfigById((String) ((Leaf) item).getValue()).getScope());
                    } catch (CouldNotPerformException ex) {
                        text = ((Leaf) item).getValue().toString();
                    }
                } else if ("member_id".equals(item.getDescriptor()) && ((LeafContainer) item).getParent().getBuilder() instanceof UserGroupConfig.Builder) {
                    try {
                        text = new UserConfigComboBoxConverter().getText(remotePool.getUserRemote().getUserConfigById((String) ((Leaf) item).getValue()));
                    } catch (CouldNotPerformException ex) {
                        text = ((Leaf) item).getValue().toString();
                    }
                } else {
                    text = ((Leaf) item).getValue().toString();
                }
            }

            if (((LeafContainer) item).getEditable()) {
                setText(text);
                setGraphic(null);
            } else {
                setGraphic(SelectableLabel.makeSelectable(new Label(text)));
            }
        }

        if (item instanceof GenericNodeContainer) {
            GenericNodeContainer container = (GenericNodeContainer) item;
            String text = getBuilderDescription(container.getBuilder());
            if (text != null) {
                setGraphic(SelectableLabel.makeSelectable(new Label(text)));
            } else {
                setText("");
                setGraphic(null);
            }
            if (container.isSendable()) {
                updateButtonListener(container.getChanged());
                if (container.hasChanged()) {
                    setGraphic(buttonLayout);
                }

                try {
                    if ("".equals(FieldDescriptorUtil.getId(container.getBuilder()))) {
                        container.setChanged(true);
                    }
                } catch (CouldNotPerformException ex) {
                    RegistryEditor.printException(ex, logger, LogLevel.WARN);
                }
            } else {
                updateButtonListener(null);
            }
        } else {
            updateButtonListener(null);
        }

        // ==================== TODO:tamino redesign
        if (item instanceof GenericGroupContainer) {
            if (((GenericGroupContainer) item).getParent().getValue() instanceof GenericGroupContainer) {
                GenericGroupContainer parent = (GenericGroupContainer) ((GenericGroupContainer) item).getParent().getValue();
                if (parent.getFieldGroup() instanceof DeviceClassItemDescriptorProvider) {
                    try {
                        int index = parent.getChildren().indexOf(item);
                        String text = remotePool.getDeviceRemote().getDeviceClassById((String) parent.getValues().get(index)).getDescription();
                        setGraphic(SelectableLabel.makeSelectable(new Label(text)));
                    } catch (CouldNotPerformException ex) {
                        RegistryEditor.printException(ex, logger, LogLevel.DEBUG);
                    }
                }
            }
        }
        // ============================================
    }

    public String getBuilderDescription(Message.Builder builder) {
        if (builder instanceof EntryType.Entry.Builder) {
            EntryType.Entry.Builder entry = (EntryType.Entry.Builder) builder;
            return entry.getKey() + " = " + entry.getValue();
        } else if (Configuration.isSendable(builder)) {
            try {
                return FieldDescriptorUtil.getDescription(builder);
            } catch (CouldNotPerformException ex) {
            }
        }
        return null;
    }

    public LeafContainer getLeaf() {
        return leaf;
    }

    private void updateButtonListener(SimpleObjectProperty<Boolean> property) {
        if (changed != null) {
            changed.removeListener(changeListener);
        }
        changed = property;
        if (changed != null) {
            changed.addListener(changeListener);
        }
    }

    private class ApplyEventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            logger.info("new apply event");
            Thread thread = new Thread(
                    new Task<Boolean>() {
                        @Override
                        protected Boolean call() throws Exception {
                            GenericNodeContainer container = (GenericNodeContainer) getItem();
                            Message msg = null;
                            try {
                                msg = container.getBuilder().build();
                                if (remotePool.contains(msg)) {
                                    remotePool.update(msg);
                                } else {
                                    container.getParent().getChildren().remove(container);
                                    remotePool.register(msg);
                                }
                                container.setChanged(false);
                            } catch (Exception ex) {
                                RegistryEditor.printException(ex, logger, LogLevel.ERROR);
                                logger.warn("Could not register or update message [" + msg + "]", ex);
                            }
                            return true;
                        }
                    });
            thread.setDaemon(true);
            thread.start();
        }
    }

    private class CancelEventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            Thread thread = new Thread(
                    new Task<Boolean>() {
                        @Override
                        protected Boolean call() throws Exception {
                            GenericNodeContainer container = (GenericNodeContainer) getItem();
                            try {
                                if ("".equals(FieldDescriptorUtil.getId(container.getBuilder()))) {
                                    container.getParent().getChildren().remove(container);
                                } else {
                                    int index = container.getParent().getChildren().indexOf(container);
                                    GenericNodeContainer oldNode = new GenericNodeContainer(container.getBuilder().getDescriptorForType().getName(), remotePool.getById(FieldDescriptorUtil.getId(container.getBuilder()), container.getBuilder()));
                                    RegistryTreeTableView.expandEqually(container, oldNode);
                                    container.getParent().getChildren().set(index, oldNode);
                                }
                            } catch (Exception ex) {
                                RegistryEditor.printException(ex, logger, LogLevel.ERROR);
                                logger.warn("Could not cancel update of [" + container.getBuilder() + "]", ex);
                            }
                            return true;
                        }
                    });
            thread.setDaemon(true);
            thread.start();
        }
    }

    private class ChangedListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    if (newValue) {
                        setGraphic(buttonLayout);
                    } else {
                        setGraphic(null);
                    }
                }
            });
        }
    }
}
