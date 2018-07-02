package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.NumberFilteredTextField;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class NumberEditingGraphic extends AbstractTextEditingGraphic<NumberFilteredTextField, Double> {

    public NumberEditingGraphic(ValueType<Double> valueType, TreeTableCell<Object, Object> treeTableCell) {
        super(new NumberFilteredTextField(), valueType, treeTableCell);
    }

    @Override
    protected Double getCurrentValue() {
        return Double.parseDouble(getControl().getText());
    }

    @Override
    protected void init(Double value) {
        getControl().setText(value.toString());
    }

    @Override
    protected void commitEdit() {
        if (getControl().validateDecimalField()) {
            super.commitEdit();
        }
    }
}
