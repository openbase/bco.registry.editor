package org.dc.bco.registry.editor.visual.cell.editing;

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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.dc.bco.registry.editor.visual.cell.ValueCell;

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
