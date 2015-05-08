/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.csra.dm.remote.DeviceRegistryRemote;
import de.citec.csra.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.struct.leaf.Leaf;
import de.citec.csra.re.struct.leaf.LeafContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.csra.re.struct.node.SendableNode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author thuxohl
 */
public abstract class ValueCell extends RowCell {
        
    private final TextField stringTextField;
    private final TextField decimalTextField;
    private final ComboBox enumComboBox;
    private final DatePicker longDatePicker;
    private final DateFormat converter = new SimpleDateFormat("dd/MM/yyyy");
    protected final Button applyButton;
    protected LeafContainer leaf;
    
    public ValueCell(DeviceRegistryRemote deviceRegistryRemote, LocationRegistryRemote locationRegistryRemote) {
        super(deviceRegistryRemote, locationRegistryRemote);
        applyButton = new Button("Apply Changes");
        applyButton.setVisible(false);
        stringTextField = new TextField();
        stringTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue && !leaf.getValue().equals(stringTextField.getText())) {
                    leaf.setValue(stringTextField.getText());
                    // even though commit is called the text property won't change fast enough without this line?!?
                    setText(stringTextField.getText());
                    commitEdit(leaf);
                }
            }
        });
        stringTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ESCAPE)) {
                    cancelEdit();
                } else if (event.getCode().equals(KeyCode.ENTER)) {
                    leaf.setValue(stringTextField.getText());
                    commitEdit(leaf);
                }
            }
        });
        
        enumComboBox = new ComboBox();
        enumComboBox.setOnAction(new EventHandler() {
            
            @Override
            public void handle(Event event) {
                if (enumComboBox.getSelectionModel().getSelectedItem() != null && !leaf.getValue().equals(enumComboBox.getSelectionModel().getSelectedItem())) {
                    leaf.setValue(enumComboBox.getSelectionModel().getSelectedItem());
                    commitEdit(leaf);
                }
            }
        });
        
        decimalTextField = new TextField() {
            @Override
            public void replaceText(int start, int end, String text) {
                if (text.matches("[0-9.]") || text.equals("")) {
                    super.replaceText(start, end, text);
                }
            }
            
            @Override
            public void replaceSelection(String text) {
                if (text.matches("[0-9.]") || text.equals("")) {
                    super.replaceSelection(text);
                }
            }
        };
        decimalTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ESCAPE)) {
                    cancelEdit();
                } else if (event.getCode().equals(KeyCode.ENTER)) {
                    if (leaf.getValue() instanceof Float) {
                        float parsedValue = Float.parseFloat(decimalTextField.getText());
                        leaf.setValue(parsedValue);
                    } else if (leaf.getValue() instanceof Double) {
                        double parsedValue = Double.parseDouble(decimalTextField.getText());
                        leaf.setValue(parsedValue);
                    }
                    commitEdit(leaf);
                }
            }
        });
        decimalTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                double parsedValue = 0;
                if (leaf.getValue() instanceof Float) {
                    parsedValue = Float.parseFloat(decimalTextField.getText());
                } else if (leaf.getValue() instanceof Double) {
                    parsedValue = Double.parseDouble(decimalTextField.getText());
                }
                if (!newValue && !leaf.getValue().equals(parsedValue)) {
                    leaf.setValue(parsedValue);
                    // even though commit is called the text property won't change fast enough without this line?!?
                    setText(Double.toString(parsedValue));
                    commitEdit(leaf);
                }
            }
        });
        
        longDatePicker = new DatePicker();
        longDatePicker.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                if (longDatePicker.getValue() != null && longDatePicker.getValue().toEpochDay() != (Long) leaf.getValue()) {
                    leaf.setValue(longDatePicker.getValue().toEpochDay() * 24 * 60 * 60 * 1000);
                    setText(converter.format(new Date((Long) leaf.getValue())));
                    commitEdit(leaf);
                }
            }
        });
    }
    
    @Override
    public void startEdit() {
        super.startEdit();
        
        if (getItem() instanceof Leaf) {
            leaf = ((LeafContainer) getItem());
            
            if (leaf.getValue() instanceof String) {
                stringTextField.setText((String) leaf.getValue());
                setEditingGraphic(stringTextField);
            } else if (leaf.getValue() instanceof Enum) {
                if (!((LeafContainer) leaf).getParent().getDescriptor().equals("service_config")) {
                    enumComboBox.setItems(FXCollections.observableArrayList(leaf.getValue().getClass().getEnumConstants()));
                    enumComboBox.setValue(leaf.getValue());
                    setEditingGraphic(enumComboBox);
                }
            } else if (leaf.getValue() instanceof Float || leaf.getValue() instanceof Double) {
                decimalTextField.setText(((Float) leaf.getValue()).toString());
                decimalTextField.setText(leaf.getValue().toString());
                setEditingGraphic(decimalTextField);
            } else if (leaf.getValue() instanceof Long) {
                Date date = new Date((Long) leaf.getValue());
                longDatePicker.setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                setEditingGraphic(longDatePicker);
            }
        }
    }
    
    public void setEditingGraphic(javafx.scene.Node node) {
        if (leaf.getEditable()) {
            super.setGraphic(node);
        }
    }
    
    @Override
    public void commitEdit(Node newValue) {
        super.commitEdit(newValue);
    }
    
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        graphicProperty().setValue(null);
    }
    
    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);
        
        if (empty) {
            setGraphic(null);
            setText("");
            setContextMenu(null);
        } else if (item instanceof Leaf) {
            graphicProperty().setValue(null);
            if (((Leaf) item).getValue() instanceof Long) {
                setText(converter.format(new Date((Long) ((Leaf) item).getValue())));
            } else if ((((Leaf) item).getValue() != null)) {
                setText(((Leaf) item).getValue().toString());
            }
        } else if (item instanceof SendableNode) {
            applyButton.setVisible(((SendableNode) item).hasChanged());
        }
    }
}
