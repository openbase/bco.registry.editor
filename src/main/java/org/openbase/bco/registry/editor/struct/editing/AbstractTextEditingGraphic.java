package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
abstract class AbstractTextEditingGraphic<TF extends TextField, V> extends AbstractEditingGraphic<TF, V> {

    AbstractTextEditingGraphic(final TF textfield, final ValueType<V> valueType, final TreeTableCell<Object, Object> treeTableCell) {
        super(textfield, valueType, treeTableCell);
        getControl().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                getControl().selectAll();
            }
        });
        getControl().setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    treeTableCell.cancelEdit();
                    break;
                case ENTER:
                    commitEdit();
            }
        });
    }
}
