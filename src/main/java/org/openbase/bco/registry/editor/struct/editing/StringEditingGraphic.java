package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class StringEditingGraphic extends AbstractTextEditingGraphic<TextField, String> {

    public StringEditingGraphic(final ValueType<String> valueType, final TreeTableCell<Object, Object> treeTableCell) {
        super(new TextField(), valueType, treeTableCell);
    }

    @Override
    protected String getCurrentValue() {
        return getControl().getText();
    }

    @Override
    protected void init(final String value) {
        getControl().setText(value);
    }
}
