package org.openbase.bco.registry.editor.visual.cell;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2022 openbase.org
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

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.HBox;
import org.openbase.bco.registry.editor.struct.ValueType;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DescriptionCell extends TreeTableCell<ValueType, ValueType> {

    public DescriptionCell() {
        super();
        setText(null);
    }

    @Override
    protected void updateItem(ValueType item, boolean empty) {
        super.updateItem(item, empty);

        if (!empty && item != null) {
            final Node descriptionGraphic = item.getDescriptionGraphic();

            // TODO: this is a work around because OpenJFX currently does not display graphics correctly, remove if this bug is fixed
            // count the number of parents and add according spacing on the right
            int parentNumber = 0;
            TreeItem treeItem = item.getTreeItem().getParent();
            while (treeItem != null) {
                treeItem = treeItem.getParent();
                parentNumber++;
            }
            HBox hBox = new HBox(descriptionGraphic);
            hBox.setPadding(new Insets(0, 0, 0, 15 * parentNumber));

            setGraphic(hBox);
        } else {
            setGraphic(null);
        }
    }
}
