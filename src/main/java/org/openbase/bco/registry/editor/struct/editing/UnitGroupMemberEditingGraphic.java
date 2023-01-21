package org.openbase.bco.registry.editor.struct.editing;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2023 openbase.org
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
import org.openbase.bco.registry.editor.struct.preset.UnitGroupConfigTreeItem;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.type.domotic.service.ServiceDescriptionType.ServiceDescription;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class UnitGroupMemberEditingGraphic extends AbstractUnitConfigEditingGraphic {

    public UnitGroupMemberEditingGraphic(final ValueType<String> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(valueType, treeTableCell);
    }

    @Override
    protected List<UnitConfig> getMessages() throws CouldNotPerformException {
        // get the unit group config parent
        final UnitGroupConfigTreeItem unitGroupConfigTreeItem = (UnitGroupConfigTreeItem) getValueType().getTreeItem().getParent().getParent();
        // create a list of all internal service types
        final List<ServiceType> serviceTypeList = new ArrayList<>();
        for (final ServiceDescription serviceDescription : unitGroupConfigTreeItem.getBuilder().getServiceDescriptionList()) {
            if (!serviceTypeList.contains(serviceDescription.getServiceType())) {
                serviceTypeList.add(serviceDescription.getServiceType());
            }
        }
        // retrieve all units with the same unit type and service types as the group
        final List<UnitConfig> unitConfigs = Registries.getUnitRegistry().getUnitConfigsByUnitTypeAndServiceTypes(unitGroupConfigTreeItem.getBuilder().getUnitType(), serviceTypeList);
        // remove all units which are already members of the group
        for (final String memberId : unitGroupConfigTreeItem.getBuilder().getMemberIdList()) {
            if (memberId.equals(getValueType().getValue())) {
                continue;
            }
            unitConfigs.remove(Registries.getUnitRegistry().getUnitConfigById(memberId));
        }

        // remove all units not at the same location as the group
        final UnitConfigTreeItem unitConfigTreeItem = (UnitConfigTreeItem) unitGroupConfigTreeItem.getParent();
        if(!unitConfigTreeItem.getBuilder().getPlacementConfig().getLocationId().isEmpty()) {
            for (final UnitConfig unitConfig : new ArrayList<>(unitConfigs)) {
                if (!isInLocation(unitConfig, unitConfigTreeItem.getBuilder().getPlacementConfig().getLocationId())) {
                    unitConfigs.remove(unitConfig);
                }
            }
        }

        return unitConfigs;
    }

    private boolean isInLocation(final UnitConfig unitConfig, final String locationId) throws CouldNotPerformException {
        if (unitConfig.getUnitType() != UnitType.LOCATION) {
            return isInLocation(Registries.getUnitRegistry().getUnitConfigById(unitConfig.getPlacementConfig().getLocationId()), locationId);
        }

        if (locationId.equals(unitConfig.getId())) {
            return true;
        }

        if (unitConfig.getLocationConfig().getRoot()) {
            return false;
        }

        return isInLocation(Registries.getUnitRegistry().getUnitConfigById(unitConfig.getPlacementConfig().getLocationId()), locationId);
    }
}
