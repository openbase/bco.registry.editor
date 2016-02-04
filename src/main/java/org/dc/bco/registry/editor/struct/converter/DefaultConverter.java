package org.dc.bco.registry.editor.struct.converter;

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

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import java.util.HashMap;
import java.util.Map;
import org.dc.bco.registry.editor.util.FieldDescriptorUtil;
import org.dc.jul.exception.CouldNotPerformException;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class DefaultConverter implements Converter {

    private final GeneratedMessage.Builder builder;

    public DefaultConverter(GeneratedMessage.Builder builder) {
        this.builder = builder;
    }

    @Override
    public void updateBuilder(String fieldName, Object value) throws CouldNotPerformException {
        try {
            builder.setField(FieldDescriptorUtil.getFieldDescriptor(fieldName, builder), value);
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not update field [" + fieldName + "," + value + "]", ex);
        }
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> fieldMap = new HashMap<>();
        for (Descriptors.FieldDescriptor field : builder.getDescriptorForType().getFields()) {
            fieldMap.put(field.getName(), builder.getField(field));
        }
        return fieldMap;
    }
}
