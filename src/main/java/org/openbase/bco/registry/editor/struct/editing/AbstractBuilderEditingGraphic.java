package org.openbase.bco.registry.editor.struct.editing;

import com.google.protobuf.Message;
import javafx.scene.Node;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractBuilderEditingGraphic<GRAPHIC extends Node, V extends Message.Builder> extends AbstractEditingGraphic<GRAPHIC, V> {

    AbstractBuilderEditingGraphic(GRAPHIC control, ValueType<V> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(control, valueType, treeTableCell);
    }

    @Override
    protected void commitEdit() {
        if(validate()) {
            super.commitEdit();
        }
    }

    @Override
    protected V getCurrentValue() {
        updateBuilder(getValueType().getValue());
        return getValueType().getValue();
    }

    protected boolean validate() {
        return true;
    }

    protected abstract void updateBuilder(V builder);
}
