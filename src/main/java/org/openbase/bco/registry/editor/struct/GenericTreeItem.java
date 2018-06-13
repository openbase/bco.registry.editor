package org.openbase.bco.registry.editor.struct;

import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class GenericTreeItem<V> extends TreeItem<ValueType> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final FieldDescriptor fieldDescriptor;

    public GenericTreeItem(final FieldDescriptor fieldDescriptor, final V value) {
        this.fieldDescriptor = fieldDescriptor;

        // TODO: build default value type
        this.setValue(new ValueType<>(value));
    }

    @SuppressWarnings("unchecked")
    public V getInternalValue() {
        return (V) getValue().getValue();
    }
}
