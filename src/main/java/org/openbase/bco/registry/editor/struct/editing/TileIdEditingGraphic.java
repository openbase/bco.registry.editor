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

import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueListTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.connection.ConnectionConfigType.ConnectionConfig;
import org.openbase.type.domotic.unit.location.LocationConfigType.LocationConfig.LocationType;

import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class TileIdEditingGraphic extends AbstractUnitConfigEditingGraphic {

    public TileIdEditingGraphic(ValueType<String> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(valueType, treeTableCell);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<UnitConfig> getMessages() throws CouldNotPerformException {
        final ValueListTreeItem<ConnectionConfig.Builder> parent = (ValueListTreeItem<ConnectionConfig.Builder>) getValueType().getTreeItem().getParent();

        final List<UnitConfig> tileList = Registries.getUnitRegistry().getLocationUnitConfigsByLocationType(LocationType.TILE);
        for (final String tileId : parent.getBuilder().getTileIdList()) {
            if (tileId.equals(getValueType().getValue())) {
                continue;
            }

            tileList.remove(Registries.getUnitRegistry().getUnitConfigById(tileId));
        }
        return tileList;
    }
}
