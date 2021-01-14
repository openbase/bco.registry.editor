package org.openbase.bco.registry.editor.struct;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2021 openbase.org
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
import javafx.scene.control.TreeTableCell;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ValueType<V extends Object> {

    private final GenericTreeItem treeItem;
    private V value;

    public ValueType(final V value, final GenericTreeItem treeItem) {
        this.value = value;
        this.treeItem = treeItem;
    }

    public ValueType<V> createNew(final V value) {
        return new ValueType<>(value, treeItem);
    }

    public Node getDescriptionGraphic() {
        return treeItem.getDescriptionGraphic();
    }

    public String getDescriptionText() {
        return treeItem.getDescriptionText();
    }

    public Node getValueGraphic() {
        return treeItem.getValueGraphic();
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Node getEditingGraphic(final TreeTableCell<ValueType<V>, ValueType<V>> cell) {
        return treeItem.getEditingGraphic(cell);
    }

    public boolean isEditable() {
        return treeItem.isEditable();
    }

    public GenericTreeItem getTreeItem() {
        return treeItem;
    }
}
