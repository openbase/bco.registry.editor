package org.openbase.bco.registry.editor.struct.converter;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2017 openbase.org
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
import org.openbase.bco.registry.editor.struct.converter.filter.DefaultFilter;
import org.openbase.bco.registry.editor.struct.converter.filter.Filter;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DefaultConverter implements Converter {

    private final GeneratedMessage.Builder builder;
    private final Filter filter;

    public DefaultConverter(GeneratedMessage.Builder builder) {
        this(builder, new DefaultFilter());
    }

    public DefaultConverter(GeneratedMessage.Builder builder, Filter filter) {
        this.builder = builder;
        this.filter = filter;
    }

    @Override
    public void updateBuilder(String fieldName, Object value) throws CouldNotPerformException {
        try {
            builder.setField(ProtoBufFieldProcessor.getFieldDescriptor(builder, fieldName), value);
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not update field [" + fieldName + "," + value + "]", ex);
        }
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> fieldMap = new HashMap<>();
        for (Descriptors.FieldDescriptor field : builder.getDescriptorForType().getFields()) {
            if (filter.filter(field)) {
                continue;
            }

            fieldMap.put(field.getName(), builder.getField(field));
        }
        return fieldMap;
    }
}
