package org.openbase.bco.registry.editor.struct.editing.util;

import javafx.scene.control.TextField;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class NumberFilteredTextField extends TextField {

    private static final String VALID_DECIMAL_REGEX = "\\-{0,1}((\\.[0-9]+|[0-9]+\\.){0,1})([0-9]*){0,1}";
    private static final String INPUT_CHAR_DECIMAL_REGEX = "(\\-{0,1}[0-9]*\\.{0,1}[0-9]*)";

    @Override
    public void replaceText(int start, int end, String text) {
        if (text.matches(INPUT_CHAR_DECIMAL_REGEX) || text.isEmpty()) {
            super.replaceText(start, end, text);
        }
        validateDecimalField();
        super.replaceText(start, end, text);
    }

    @Override
    public void replaceSelection(String text) {
        if (text.matches(INPUT_CHAR_DECIMAL_REGEX) || text.isEmpty()) {
            super.replaceSelection(text);
        }
        validateDecimalField();
    }

    public boolean validateDecimalField() {
        if (!this.getText().matches(VALID_DECIMAL_REGEX)) {
            this.setStyle("-fx-text-inner-color: red;");
            return false;
        }
        this.setStyle("-fx-text-inner-color: black;");
        return true;
    }
}
