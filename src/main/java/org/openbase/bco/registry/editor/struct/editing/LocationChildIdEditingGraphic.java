package org.openbase.bco.registry.editor.struct.editing;

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

import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.preset.LocationConfigTreeItem;
import org.openbase.bco.registry.editor.struct.preset.UnitConfigTreeItem;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import rst.domotic.unit.location.LocationConfigType.LocationConfig.LocationType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LocationChildIdEditingGraphic extends AbstractUnitConfigEditingGraphic {

    public LocationChildIdEditingGraphic(final ValueType<String> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(valueType, treeTableCell);
    }

    @Override
    protected List<UnitConfig> getMessages() throws CouldNotPerformException {
        final List<UnitConfig> unitConfigs = Registries.getUnitRegistry().getUnitConfigs(UnitType.LOCATION);
        final LocationConfigTreeItem locationTreeItem = (LocationConfigTreeItem) getValueType().getTreeItem().getParent().getParent();
        final UnitConfigTreeItem locationUnitTreeItem = (UnitConfigTreeItem) locationTreeItem.getParent();

        final LocationType type = locationTreeItem.getBuilder().getType();
        for (final UnitConfig unitConfig : new ArrayList<>(unitConfigs)) {
            if (unitConfig.getId().equals(getValueType().getValue())) {
                continue;
            }

            switch (type) {
                case ZONE:
                    if (unitConfig.getLocationConfig().getType() == LocationType.REGION) {
                        unitConfigs.remove(unitConfig);
                        continue;
                    }
                    break;
                case TILE:
                case REGION:
                    if (unitConfig.getLocationConfig().getType() != LocationType.REGION) {
                        unitConfigs.remove(unitConfig);
                        continue;
                    }
            }

            if (locationTreeItem.getBuilder().getChildIdList().contains(unitConfig.getId())) {
                unitConfigs.remove(unitConfig);
                continue;
            }

            if (unitConfig.getId().equals(locationUnitTreeItem.getBuilder().getId())) {
                unitConfigs.remove(unitConfig);
            }
        }

        return unitConfigs;
    }
}
