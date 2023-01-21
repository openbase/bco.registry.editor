package org.openbase.bco.registry.editor.struct.editing.util;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2023 openbase.org
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
public abstract class ValidatedTextField extends FocusedTextField {

    @Override
    public void replaceText(int start, int end, String text) {
        super.replaceText(start, end, text);
        validate();
    }

    @Override
    public void replaceSelection(String text) {
        super.replaceSelection(text);
        validate();
    }

    public boolean validate() {
        if (!internalValidate()) {
            this.setStyle("-fx-text-inner-color: red;");
            return false;
        }
        this.setStyle("-fx-text-inner-color: black;");
        return true;
    }

    protected abstract boolean internalValidate();
}
