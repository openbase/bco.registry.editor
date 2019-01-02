package org.openbase.bco.registry.editor.struct.editing;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2019 openbase.org
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.HBox;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.type.domotic.authentication.PermissionType.Permission;
import org.openbase.type.domotic.authentication.PermissionType.Permission.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class PermissionEditingGraphic extends AbstractBuilderEditingGraphic<HBox, Permission.Builder> {

    private CheckBox access, read, write;

    public PermissionEditingGraphic(ValueType<Builder> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new HBox(), valueType, treeTableCell);
    }

    @Override
    protected boolean updateBuilder(Builder builder) {
        boolean changed = false;

        if(builder.getAccess() != access.isSelected()) {
            builder.setAccess(access.isSelected());
            changed = true;
        }

        if(builder.getRead() != read.isSelected()) {
            builder.setRead(read.isSelected());
            changed = true;
        }

        if(builder.getWrite() != write.isSelected()) {
            builder.setWrite(write.isSelected());
            changed = true;
        }

        return changed;
    }

    @Override
    protected void init(Builder value) {
        access = new CheckBox("Access");
        read = new CheckBox("Read");
        write = new CheckBox("Write");

        access.setSelected(value.getAccess());
        read.setSelected(value.getRead());
        write.setSelected(value.getWrite());

        getControl().getChildren().addAll(access, read, write);
    }
}
