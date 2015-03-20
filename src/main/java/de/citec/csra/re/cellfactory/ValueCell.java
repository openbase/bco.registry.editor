/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.csra.dm.remote.DeviceRegistryRemote;
import de.citec.csra.re.struct.leaf.Leaf;
import de.citec.csra.re.struct.node.Node;
import java.time.ZoneId;
import java.util.Date;
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
    protected final Button applyButton;
    protected Leaf leaf;

    public ValueCell(DeviceRegistryRemote remote) {
        super(remote);
        applyButton = new Button("Apply Changes");
        applyButton.setVisible(true);

        stringTextField = new TextField();
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
                    float parseFloat = Float.parseFloat(decimalTextField.getText());
                    leaf.setValue(parseFloat);
                    commitEdit(leaf);
                }
            }
        });

        longDatePicker = new DatePicker();
        longDatePicker.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                leaf.setValue(longDatePicker.getValue().toEpochDay());
            }
        });
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (getItem() instanceof Leaf) {
            leaf = ((Leaf) getItem());

            if (leaf.getValue() instanceof String) {
                stringTextField.setText((String) leaf.getValue());
                graphicProperty().setValue(stringTextField);
            } else if (leaf.getValue() instanceof Enum) {
                enumComboBox.setItems(FXCollections.observableArrayList(leaf.getValue().getClass().getEnumConstants()));
                enumComboBox.setValue(leaf.getValue());
                graphicProperty().setValue(enumComboBox);
            } else if (leaf.getValue() instanceof Float) {
                decimalTextField.setText(((Float) leaf.getValue()).toString());
                graphicProperty().setValue(decimalTextField);
            } else if (leaf.getValue() instanceof Long) {
                Date date = new Date((Long) leaf.getValue());
                longDatePicker.setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                setGraphic(longDatePicker);
            }
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
            graphicProperty().setValue(null);
            textProperty().setValue("");
            setContextMenu(null);
        } else if (item instanceof Leaf) {
            graphicProperty().setValue(null);
            if (((Leaf) item).getValue() instanceof Long) {
                setText((new Date((Long) ((Leaf) item).getValue())).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
            } else {
                textProperty().setValue(((Leaf) item).getValue().toString());
            }
            setContextMenu(null);
        }
    }

}
