package org.openbase.bco.registry.editor.struct;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ValueType<V extends Object> {

    private final V value;
    private final String description;
    private final String valueDescription;
    // create an editing graphic for this value type
    private final Object editingGraphFactory;

    public ValueType(final V value) {
        this.value = value;
        this.description = "";
        this.valueDescription = "";
        this.editingGraphFactory = null;
    }

    public String getDescription() {
        return description;
    }

    public String getValueDescription() {
        return valueDescription;
    }

    public V getValue() {
        return value;
    }
}
