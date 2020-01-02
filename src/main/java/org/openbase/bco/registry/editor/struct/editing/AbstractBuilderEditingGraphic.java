package org.openbase.bco.registry.editor.struct.editing;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2020 openbase.org
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

import com.google.protobuf.Message;
import javafx.scene.Node;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractBuilderEditingGraphic<GRAPHIC extends Node, V extends Message.Builder> extends AbstractEditingGraphic<GRAPHIC, V> {

    private boolean valueHasChanged;

    AbstractBuilderEditingGraphic(GRAPHIC control, ValueType<V> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(control, valueType, treeTableCell);

        this.valueHasChanged = false;
    }

    @Override
    protected boolean valueHasChanged() {
        return valueHasChanged;
    }

    @Override
    public void commitEdit() {
        if (validate()) {
            super.commitEdit();
        }
    }

    @Override
    protected V getCurrentValue() {
        valueHasChanged = updateBuilder(getValueType().getValue());
        return getValueType().getValue();
    }

    protected boolean validate() {
        return true;
    }

    protected abstract boolean updateBuilder(V builder);
}
