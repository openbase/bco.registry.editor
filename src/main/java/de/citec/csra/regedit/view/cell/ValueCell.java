/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.cell;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Message;
import de.citec.csra.regedit.RegistryEditor;
import de.citec.csra.regedit.struct.GenericGroupContainer;
import de.citec.csra.regedit.view.cell.editing.DecimalTextField;
import de.citec.csra.regedit.view.cell.editing.EnumComboBox;
import de.citec.csra.regedit.view.cell.editing.LongDatePicker;
import de.citec.csra.regedit.view.cell.editing.MessageComboBox;
import de.citec.csra.regedit.view.cell.editing.StringTextField;
import de.citec.csra.regedit.view.cell.editing.ValueCheckBox;
import de.citec.csra.regedit.struct.GenericNodeContainer;
import de.citec.csra.regedit.struct.Leaf;
import de.citec.csra.regedit.struct.LeafContainer;
import de.citec.csra.regedit.struct.Node;
import de.citec.csra.regedit.struct.consistency.Configuration;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
import de.citec.csra.regedit.util.SelectableLabel;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.exception.printer.LogLevel;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import rst.configuration.EntryType;
import rst.homeautomation.device.DeviceConfigType;

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
    
    private javafx.scene.Node getEditingGraphic() {
        
        javafx.scene.Node graphic = null;
        Message type = MessageComboBox.getMessageEnumBoxType(leaf.getDescriptor());
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
                text = ((Leaf) item).getValue().toString();
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
                    if ("".equals(FieldDescriptorUtil.getId(container.getBuilder().build()))) {
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
        
        if (item instanceof GenericGroupContainer) {
            Descriptors.FieldDescriptor groupedField = null;
            if (((GenericGroupContainer) item).getParent().getValue() instanceof GenericGroupContainer) {
                GenericGroupContainer parent = (GenericGroupContainer) ((GenericGroupContainer) item).getParent().getValue();
                groupedField = parent.getFieldGroup().getFieldDescriptors()[0];
            }
            Descriptors.FieldDescriptor deviceClassIdfield = FieldDescriptorUtil.getFieldDescriptor(DeviceConfigType.DeviceConfig.DEVICE_CLASS_ID_FIELD_NUMBER, DeviceConfigType.DeviceConfig.getDefaultInstance());
            if (deviceClassIdfield.equals(groupedField)) {
                try {
                    String text = remotePool.getDeviceRemote().getDeviceClassById(item.getDescriptor()).getDescription();
                    setGraphic(SelectableLabel.makeSelectable(new Label(text)));
                } catch (CouldNotPerformException ex) {
                    RegistryEditor.printException(ex, logger, LogLevel.DEBUG);
                }
            }
        }
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
            Thread thread = new Thread(
                    new Task<Boolean>() {
                        @Override
                        protected Boolean call() throws Exception {
                            RegistryEditor.setModified(false);
                            GenericNodeContainer container = (GenericNodeContainer) getItem();
                            Message msg = container.getBuilder().build();
                            logger.info("Now registering/updating msg [" + msg + "]");
                            try {
                                if (remotePool.contains(msg)) {
                                    remotePool.update(msg);
                                } else {
                                    remotePool.register(msg);
                                }
                                container.setChanged(false);
                            } catch (CouldNotPerformException ex) {
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
                            RegistryEditor.setModified(false);
                            GenericNodeContainer container = (GenericNodeContainer) getItem();
                            Message msg = container.getBuilder().build();
                            try {
                                if ("".equals(FieldDescriptorUtil.getId(msg))) {
                                    container.getParent().getChildren().remove(container);
                                } else {
                                    int index = container.getParent().getChildren().indexOf(container);
                                    container.getParent().getChildren().set(index, new GenericNodeContainer(msg.getClass().getSimpleName(), remotePool.getById(FieldDescriptorUtil.getId(msg), msg)));
                                }
                            } catch (CouldNotPerformException ex) {
                                RegistryEditor.printException(ex, logger, LogLevel.ERROR);
                                logger.warn("Could not cancel update of [" + msg + "]", ex);
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
