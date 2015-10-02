/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.cell.editing;

import de.citec.csra.regedit.view.cell.ValueCell;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class DecimalTextField extends TextField {

    public static final String VALID_DECIMAL_REGEX = "\\-{0,1}((\\.[0-9]+|[0-9]+\\.){0,1})([0-9]*){0,1}";
    public static final String INPUT_CHAR_DECIMAL_REGEX = "(\\-{0,1}[0-9]*\\.{0,1}[0-9]*)";

    public DecimalTextField(ValueCell cell, String text) {
        super();
        setText(text);
        setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ESCAPE)) {
                    cell.cancelEdit();
                } else if (event.getCode().equals(KeyCode.ENTER)) {

                    if (!validateDecimalField()) {
                        return;
                    }

                    double parsedValue = 0;
                    if (cell.getLeaf().getValue() instanceof Float) {
                        parsedValue = Float.parseFloat(getText());
                        cell.getLeaf().setValue(parsedValue);
                    } else if (cell.getLeaf().getValue() instanceof Double) {
                        parsedValue = Double.parseDouble(getText());
                        cell.getLeaf().setValue(parsedValue);
                    }
                    cell.setText(Double.toString(parsedValue));
                    cell.commitEdit(cell.getLeaf());
                }
            }
        });
        focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if (!validateDecimalField()) {
                    return;
                }

                double parsedValue = 0;
                if (cell.getLeaf().getValue() instanceof Float) {
                    parsedValue = Float.parseFloat(getText());
                } else if (cell.getLeaf().getValue() instanceof Double) {
                    parsedValue = Double.parseDouble(getText());
                }
                if (!newValue && !cell.getLeaf().getValue().equals(parsedValue)) {
                    cell.getLeaf().setValue(parsedValue);
                    // even though commit is called the text property won't change fast enough without this line?!?
                    cell.setText(Double.toString(parsedValue));
                    cell.commitEdit(cell.getLeaf());
                }
            }
        });
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (text.matches(INPUT_CHAR_DECIMAL_REGEX) || text.isEmpty()) {
            super.replaceText(start, end, text);
        }
        validateDecimalField();
    }

    @Override
    public void replaceSelection(String text) {
        if (text.matches(INPUT_CHAR_DECIMAL_REGEX) || text.isEmpty()) {
            super.replaceSelection(text);
        }
        validateDecimalField();
    }

    private boolean validateDecimalField() {
        if (!this.getText().matches(VALID_DECIMAL_REGEX)) {
            this.setStyle("-fx-text-inner-color: red;");
            return false;
        }

        this.setStyle("-fx-text-inner-color: black;");
        return true;
    }
}
