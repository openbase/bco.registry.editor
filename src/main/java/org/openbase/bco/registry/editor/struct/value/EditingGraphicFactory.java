package org.openbase.bco.registry.editor.struct.value;

import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public interface EditingGraphicFactory<V> {

    javafx.scene.control.Control getEditingGraphic(final ValueType<V> valueType, final TreeTableCell<ValueType<V>, ValueType<V>> cell);
}
