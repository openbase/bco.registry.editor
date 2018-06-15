package org.openbase.bco.registry.editor.visual.cell;

import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class SecondCell extends TreeTableCell<ValueType, ValueType> {

    @Override
    protected void updateItem(ValueType item, boolean empty) {
        super.updateItem(item, empty);

        if (!empty && item != null) {
            setText(item.getDescription());
        }
    }
}
