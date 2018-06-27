package org.openbase.bco.registry.editor.struct;

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

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.value.DescriptionGenerator;
import org.openbase.bco.registry.editor.struct.value.EditingGraphicFactory;
import org.openbase.jul.exception.StackTracePrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ValueType<V extends Object> {

    private SimpleObjectProperty<V> value;
    private final DescriptionGenerator<V> descriptionGenerator;
    private final EditingGraphicFactory<V> editingGraphFactory;
    private final boolean editable;

    public ValueType(
            final V value,
            final boolean editable,
            final EditingGraphicFactory<V> editingGraphFactory,
            final DescriptionGenerator<V> descriptionGenerator) {
        this.value = new SimpleObjectProperty<>(value);
        this.editable = editable;
        this.descriptionGenerator = descriptionGenerator;
        this.editingGraphFactory = editingGraphFactory;
    }

    public String getDescription() {
        return descriptionGenerator.getDescription(getValue());
    }

    public String getValueDescription() {
        return descriptionGenerator.getValueDescription(getValue());
    }

    public V getValue() {
        return value.getValue();
    }

    public void setValue(V value) {
//        StackTracePrinter.printStackTrace("Set value to[" + value + "]", Thread.currentThread().getStackTrace(), LoggerFactory.getLogger(ValueType.class), LogLevel.INFO);
        this.value.set(value);
    }

    public SimpleObjectProperty<V> valueProperty() {
        return value;
    }

    public Control getEditingGraphic(final TreeTableCell<ValueType<V>, ValueType<V>> cell) {
        if(editingGraphFactory == null) {
            return null;
        }
        return editingGraphFactory.getEditingGraphic(this, cell);
    }

    public boolean isEditable() {
        return editable;
    }

    public SimpleObjectProperty<V> getValueProperty() {
        return value;
    }

    public DescriptionGenerator<V> getDescriptionGenerator() {
        return descriptionGenerator;
    }

    public EditingGraphicFactory<V> getEditingGraphFactory() {
        return editingGraphFactory;
    }
}
