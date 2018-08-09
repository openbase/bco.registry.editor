package org.openbase.bco.registry.editor.visual.cell;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class TestCell extends TreeTableCell<ValueType, ValueType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCell.class);

    //    private final Label label;
//    private final Label selectableLabel;
    private Node editingGraphic;

    public TestCell() {
        super();
//        this.label = new Label();
//        this.selectableLabel = SelectableLabel.makeSelectable(new Label());
    }

    @Override
    protected void updateItem(ValueType item, boolean empty) {
        super.updateItem(item, empty);

        if (!empty && item != null) {
            if (isEditing()) {
                if (editingGraphic != null) {
                    setGraphic(editingGraphic);
                }
                return;
            }

            setGraphic(item.getValueGraphic());
        } else {
            // reset text if now empty
            setGraphic(new Label(""));
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();

        editingGraphic = getTreeTableRow().getTreeItem().getValue().getEditingGraphic(this);
        if (editingGraphic != null) {
            // editing graphic equals null means not editable
            setGraphic(editingGraphic);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        updateItem(getItem(), getItem() == null);
    }
}