package org.openbase.bco.registry.editor.struct.editing;

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

import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.NumberFilteredTextField;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DoubleEditingGraphic extends AbstractTextEditingGraphic<NumberFilteredTextField, Double> {

    public DoubleEditingGraphic(ValueType<Double> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new NumberFilteredTextField(), valueType, treeTableCell);
    }

    @Override
    protected Double getCurrentValue() {
        return Double.parseDouble(getControl().getText());
    }

    @Override
    protected void init(Double value) {
        getControl().setText(value.toString());
    }
}
