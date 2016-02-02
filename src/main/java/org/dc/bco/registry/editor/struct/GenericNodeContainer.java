package org.dc.bco.registry.editor.struct;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 DivineCooperation
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
import com.google.protobuf.GeneratedMessage;
import java.util.Map.Entry;
import org.dc.bco.registry.editor.struct.consistency.Configuration;
import org.dc.bco.registry.editor.struct.converter.DefaultConverter;
import org.dc.bco.registry.editor.util.FieldDescriptorUtil;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.InstantiationException;
import org.dc.jul.exception.NotAvailableException;

/**
 *
 * @author thuxohl
 * @param <MB>
 */
public class GenericNodeContainer<MB extends GeneratedMessage.Builder> extends NodeContainer<MB> {

    public GenericNodeContainer(String descriptor, final MB builder) throws InstantiationException {
        super(descriptor, builder);
        try {

            for (Entry<String, Object> entry : converter.getFields().entrySet()) {
                if (converter instanceof DefaultConverter) {
                    registerElement(FieldDescriptorUtil.getFieldDescriptor(entry.getKey(), builder));
                } else {
                    registerElement(entry.getKey(), entry.getValue());
                }
            }
        } catch (InstantiationException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    public GenericNodeContainer(final int fieldNumber, final MB builder) throws InstantiationException {
        this(FieldDescriptorUtil.getFieldDescriptor(fieldNumber, builder), builder);
    }

    public GenericNodeContainer(final FieldDescriptor fieldDescriptor, final MB builder) throws InstantiationException {
        super(fieldDescriptor.getName(), builder);
        try {
            if (fieldDescriptor == null) {
                throw new NotAvailableException("fieldDescriptor");
            }

            for (Entry<String, Object> entry : converter.getFields().entrySet()) {
                if (converter instanceof DefaultConverter) {
                    registerElement(FieldDescriptorUtil.getFieldDescriptor(entry.getKey(), builder));
                } else {
                    registerElement(entry.getKey(), entry.getValue());
                }
            }
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    private void registerElement(FieldDescriptor field) throws InstantiationException {
        if (field.isRepeated()) {
            super.add(new GenericListContainer<>(field, builder));
        } else if (field.getType().equals(FieldDescriptor.Type.MESSAGE)) {
            super.add(new GenericNodeContainer(field, (GeneratedMessage.Builder) builder.getFieldBuilder(field)));
        } else {
            super.add(new LeafContainer(builder.getField(field), field.getName(), this, Configuration.isModifiableField(builder, field.getName())));
        }
    }

    public void registerElement(String fieldName, Object value) throws InstantiationException {
        super.add(new LeafContainer(value, fieldName, this, Configuration.isModifiableField(builder, fieldName)));
    }
}
