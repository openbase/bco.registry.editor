package org.openbase.bco.registry.editor.struct.editing;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2019 openbase.org
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

import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.MetaConfigEntryTextField;
import org.openbase.type.configuration.EntryType.Entry.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class MetaConfigEntryEditingGraphic extends AbstractBuilderEditingGraphic<TextField, Builder> {

    public MetaConfigEntryEditingGraphic(ValueType<Builder> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new MetaConfigEntryTextField(), valueType, treeTableCell);
    }

    @Override
    protected boolean updateBuilder(Builder builder) {
        // extract key and value from text field
        final String[] split = getControl().getText().split("=");
        final String newKey = split[0].replaceAll(" ", "");
        final String newValue = split[1].replaceAll(" ", "");

        // test if key or value changed
        if (builder.getKey().equals(newKey) && builder.getValue().equals(newValue)) {
            // nothing changed so return false
            return false;
        }

        // update builder and return true
        builder.setKey(newKey).setValue(newValue);
        return true;
    }

    @Override
    protected void init(Builder value) {
        getControl().setText(value.getKey() + " = " + value.getValue());
    }
}
