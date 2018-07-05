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
import javafx.scene.control.TreeItem;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.struct.value.DefaultDescriptionGenerator;
import org.openbase.bco.registry.editor.struct.value.DescriptionGenerator;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class GenericTreeItem<V> extends TreeItem<ValueType> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final FieldDescriptor fieldDescriptor;
    private boolean editable;

    public GenericTreeItem(final FieldDescriptor fieldDescriptor, final V value) throws InitializationException {
        this(fieldDescriptor, value, true);
    }

    public GenericTreeItem(final FieldDescriptor fieldDescriptor, final V value, final boolean editable) throws InitializationException {
        try {
            this.fieldDescriptor = fieldDescriptor;
            this.setValue(new ValueType<>(value, editable, getEditingGraphicFactory(), getDescriptionGenerator()));
            this.editable = editable;
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    public FieldDescriptor getFieldDescriptor() {
        return fieldDescriptor;
    }

    @SuppressWarnings("unchecked")
    V getInternalValue() {
        return (V) getValue().getValue();
    }

    protected EditingGraphicFactory getEditingGraphicFactory() throws CouldNotPerformException {
        return null;
    }

    protected DescriptionGenerator<V> getDescriptionGenerator() {
        return new DefaultDescriptionGenerator<>(fieldDescriptor);
    }

    protected boolean isEditable() {
        return editable;
    }

    @SuppressWarnings("unchecked")
    protected ValueType<V> getValueCasted() {
        return (ValueType<V>) getValue();
    }
}
