package org.openbase.bco.registry.editor.struct.editing.util;

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

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class NumberFilteredTextField extends ValidatedTextField {

    private static final String VALID_DECIMAL_REGEX = "\\-{0,1}((\\.[0-9]+|[0-9]+\\.){0,1})([0-9]*){0,1}";
    private static final String INPUT_CHAR_DECIMAL_REGEX = "(\\-{0,1}[0-9]*\\.{0,1}[0-9]*)";

    @Override
    public void replaceText(int start, int end, String text) {
        if (text.matches(INPUT_CHAR_DECIMAL_REGEX) || text.isEmpty()) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        if (text.matches(INPUT_CHAR_DECIMAL_REGEX) || text.isEmpty()) {
            super.replaceSelection(text);
        }
    }

    @Override
    protected boolean internalValidate() {
        return this.getText().matches(VALID_DECIMAL_REGEX);
    }

    public Double getAsDouble() {
        return Double.parseDouble(getText());
    }

    public Float getAsFloat() {
        return Float.parseFloat(getText());
    }
}
