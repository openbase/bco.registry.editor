package org.openbase.bco.registry.editor.struct;

import com.google.protobuf.Descriptors.FieldDescriptor;
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
    protected final FieldDescriptor fieldDescriptor;

    public GenericTreeItem(final FieldDescriptor fieldDescriptor, final V value) {
        this.fieldDescriptor = fieldDescriptor;
        this.setValue(new ValueType<>(value, true, getEditingGraphicFactory(), getDescriptionGenerator()));
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
}
