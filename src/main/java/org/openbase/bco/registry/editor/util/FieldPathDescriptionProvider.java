package org.openbase.bco.registry.editor.util;

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
