package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.NumberFilteredTextField;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class FloatEditingGraphic extends AbstractTextEditingGraphic<NumberFilteredTextField, Float> {

    public FloatEditingGraphic(ValueType<Float> valueType, TreeTableCell<Object, Object> treeTableCell) {
        super(new NumberFilteredTextField(), valueType, treeTableCell);
    }

    @Override
    protected Float getCurrentValue() {
        return Float.parseFloat(getControl().getText());
    }

    @Override
    protected void init(Float value) {
        getControl().setText(value.toString());
    }

    @Override
    protected void commitEdit() {
        if (getControl().validateDecimalField()) {
            super.commitEdit();
        }
    }
}
