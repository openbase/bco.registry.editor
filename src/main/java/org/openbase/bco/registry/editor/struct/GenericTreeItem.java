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

import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import org.openbase.bco.registry.editor.struct.value.DefaultDescriptionGenerator;
import org.openbase.bco.registry.editor.struct.value.DescriptionGenerator;
import org.openbase.bco.registry.editor.struct.value.EditingGraphicFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class GenericTreeItem<V> extends TreeItem<ValueType> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final FieldDescriptor fieldDescriptor;

    public GenericTreeItem(final FieldDescriptor fieldDescriptor, final V value) {
        this.fieldDescriptor = fieldDescriptor;
        this.setValue(new ValueType<>(value, isEditable(), getEditingGraphicFactory(), getDescriptionGenerator()));

        getValueCasted().getValueProperty().addListener(new ChangeListener<V>() {
            @Override
            public void changed(ObservableValue<? extends V> observable, V oldValue, V newValue) {
                setValue(new ValueType(newValue, isEditable(), getEditingGraphicFactory(), getDescriptionGenerator()));
            }
        });
        valueProperty().bind(getValueCasted().getValueProperty());
    }

    public FieldDescriptor getFieldDescriptor() {
        return fieldDescriptor;
    }

    @SuppressWarnings("unchecked")
    V getInternalValue() {
        return (V) getValue().getValue();
    }

    protected EditingGraphicFactory<V> getEditingGraphicFactory() {
        return null;
    }

    protected DescriptionGenerator<V> getDescriptionGenerator() {
        return new DefaultDescriptionGenerator<>(fieldDescriptor);
    }

    protected boolean isEditable() {
        return true;
    }

    protected ValueType<V> getValueCasted() {
        return (ValueType<V>) getValue();
    }
}
