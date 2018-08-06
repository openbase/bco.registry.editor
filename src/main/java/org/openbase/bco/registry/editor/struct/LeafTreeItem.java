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
import com.google.protobuf.Message;
import org.openbase.bco.registry.editor.struct.editing.*;
import org.openbase.bco.registry.editor.struct.value.DescriptionGenerator;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LeafTreeItem<V> extends GenericTreeItem<V> {

    private final Message.Builder parentBuilder;

    public LeafTreeItem(final FieldDescriptor fieldDescriptor, final V value, final Message.Builder parentBuilder) throws InitializationException {
        this(fieldDescriptor, value, parentBuilder, true);
    }

    public LeafTreeItem(final FieldDescriptor fieldDescriptor, final V value, final Message.Builder parentBuilder, final boolean editable) throws InitializationException {
        super(fieldDescriptor, value, editable);
        this.parentBuilder = parentBuilder;
        valueProperty().addListener((observable, oldValue, newValue) -> parentBuilder.setField(getFieldDescriptor(), newValue.getValue()));
    }

    @Override
    protected EditingGraphicFactory getEditingGraphicFactory() throws CouldNotPerformException {
        switch (getFieldDescriptor().getType()) {
            case BOOL:
                return EditingGraphicFactory.getInstance(BooleanEditingGraphic.class);
            case ENUM:
                return EditingGraphicFactory.getInstance(EnumEditingGraphic.class);
            case FLOAT:
                return EditingGraphicFactory.getInstance(FloatEditingGraphic.class);
            case DOUBLE:
                return EditingGraphicFactory.getInstance(DoubleEditingGraphic.class);
            case STRING:
                return EditingGraphicFactory.getInstance(StringEditingGraphic.class);
            default:
                logger.warn("Editing not supported for field with type[" + getFieldDescriptor().getType().name() + "]");
                return null;
        }
    }

    @Override
    protected DescriptionGenerator<V> getDescriptionGenerator() {
        return new DescriptionGenerator<V>() {
            @Override
            public String getValueDescription(V value) {
                return value.toString();
            }

            @Override
            public String getDescription(V value) {
                return getFieldDescriptor().getName();
            }
        };
    }
}
