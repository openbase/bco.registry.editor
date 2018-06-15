package org.openbase.bco.registry.editor.struct;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.value.DescriptionGenerator;
import org.openbase.bco.registry.editor.struct.value.EditingGraphicFactory;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ValueType<V extends Object> {

    private SimpleObjectProperty<V> value;
    private final DescriptionGenerator<V> descriptionGenerator;
    private final EditingGraphicFactory<V> editingGraphFactory;
    private final boolean editable;

    public ValueType(
            final V value,
            final boolean editable,
            final EditingGraphicFactory<V> editingGraphFactory,
            final DescriptionGenerator<V> descriptionGenerator) {
        this.value = new SimpleObjectProperty<>(value);
        this.editable = editable;
        this.descriptionGenerator = descriptionGenerator;
        this.editingGraphFactory = editingGraphFactory;
    }

    public String getDescription() {
        return descriptionGenerator.getDescription(getValue());
    }

    public String getValueDescription() {
        return descriptionGenerator.getValueDescription(getValue());
    }

    public V getValue() {
        return value.getValue();
    }

    public void setValue(V value) {
        this.value.set(value);
    }

    public Control getEditingGraphic(final TreeTableCell<ValueType<V>, ValueType<V>> cell) {
        return editingGraphFactory.getEditingGraphic(this, cell);
    }

    public boolean isEditable() {
        return editable;
    }

    public SimpleObjectProperty<V> getValueProperty() {
        return value;
    }
}
