package org.openbase.bco.registry.editor.util;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2024 openbase.org
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
import com.google.protobuf.Message.Builder;
import com.google.protobuf.MessageOrBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class FieldPathDescriptionProvider {

    private final FieldDescriptorPath fieldDescriptorPath;

    public FieldPathDescriptionProvider(final FieldDescriptorPath fieldDescriptorPath) {
        this.fieldDescriptorPath = fieldDescriptorPath;
    }

    public abstract String generateDescription(final Object value);

    public Object getValue(final MessageOrBuilder messageOrBuilder) {
        return fieldDescriptorPath.getValue(messageOrBuilder);
    }

    public void setValue(final Message.Builder builder, final Object value) {
        fieldDescriptorPath.setValue(builder, value);
    }

    public FieldDescriptorPath getFieldDescriptorPath() {
        return fieldDescriptorPath;
    }

    public Map<Object, List<Builder>> getValueBuilderMap(final List<Message.Builder> builderList) {
        final Map<Object, List<Message.Builder>> valueBuilderMap = new HashMap<>();
        for (final Message.Builder builder : builderList) {
            final Object value = getValue(builder);
            if (valueBuilderMap.containsKey(value)) {
                valueBuilderMap.get(value).add(builder);
            } else {
                final List<Message.Builder> internalList = new ArrayList<>();
                internalList.add(builder);
                valueBuilderMap.put(value, internalList);
            }
        }
        return valueBuilderMap;
    }
}
