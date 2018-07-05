package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class BooleanEditingGraphic extends AbstractEditingGraphic<CheckBox, Boolean> {

    public BooleanEditingGraphic(final ValueType<Boolean> valueType, final TreeTableCell<Object, Object> treeTableCell) {
        super(new CheckBox(), valueType, treeTableCell);
        getControl().setOnAction((event) -> commitEdit());
    }

    @Override
    protected Boolean getCurrentValue() {
        return getControl().isSelected();
    }

    @Override
    protected void init(final Boolean value) {
        getControl().setSelected(value);
    }
}
