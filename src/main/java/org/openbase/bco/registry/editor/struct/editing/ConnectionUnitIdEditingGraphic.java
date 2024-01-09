package org.openbase.bco.registry.editor.struct.editing;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2024 openbase.org
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
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.preset.UnitConfigTreeItem;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ConnectionUnitIdEditingGraphic extends AbstractUnitConfigEditingGraphic {

    public ConnectionUnitIdEditingGraphic(final ValueType<String> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(valueType, treeTableCell);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<UnitConfig> getMessages() throws CouldNotPerformException {
        // get the unit config of this connection
        final UnitConfigTreeItem unitConfigTreeItem = (UnitConfigTreeItem) getValueType().getTreeItem().getParent().getParent().getParent();

        //TODO: is this fine or get only units from all tiles this connection connects
        // get all unit configs at the same location as the connection
        final List<UnitConfig> unitConfigs = Registries.getUnitRegistry().getUnitConfigsByLocationId(unitConfigTreeItem.getBuilder().getPlacementConfig().getLocationId());
        // remove all units already at this connection
        for (final String unitId : unitConfigTreeItem.getBuilder().getConnectionConfig().getUnitIdList()) {
            if (getValueType().getValue().equals(unitId)) {
                continue;
            }

            unitConfigs.remove(Registries.getUnitRegistry().getUnitConfigById(unitId));
        }
        // remove all locations and other connections
        for (final UnitConfig unitConfig : new ArrayList<>(unitConfigs)) {
            switch (unitConfig.getUnitType()) {
                //TODO: are there other units that cannot be placed at a connection
                case LOCATION:
                case CONNECTION:
                    unitConfigs.remove(unitConfig);
                    break;
            }
        }

        return unitConfigs;
    }
}
