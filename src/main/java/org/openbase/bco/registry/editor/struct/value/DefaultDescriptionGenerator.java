package org.openbase.bco.registry.editor.struct.value;

import com.google.protobuf.Descriptors.FieldDescriptor;
import org.openbase.jul.processing.StringProcessor;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DefaultDescriptionGenerator<V> implements DescriptionGenerator<V> {

    private final String description;

    public DefaultDescriptionGenerator(FieldDescriptor fieldDescriptor) {
        this.description = StringProcessor.transformUpperCaseToCamelCase(fieldDescriptor.getName().toUpperCase());
    }

    @Override
    public String getValueDescription(V value) {
        return "";
    }

    @Override
    public String getDescription(V value) {
        return description;
    }
}
