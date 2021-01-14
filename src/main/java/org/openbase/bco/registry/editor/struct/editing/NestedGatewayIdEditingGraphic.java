package org.openbase.bco.registry.editor.struct.editing;

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

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueListTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.preset.UnitConfigTreeItem;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.pattern.ListFilter;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate;
import org.openbase.type.domotic.unit.gateway.GatewayClassType.GatewayClass;

import java.util.ArrayList;
import java.util.List;

public class NestedGatewayIdEditingGraphic extends AbstractUnitConfigEditingGraphic {

    public NestedGatewayIdEditingGraphic(ValueType<String> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(valueType, treeTableCell);
    }

    @Override
    protected List<UnitConfig> getMessages() throws CouldNotPerformException {
        final List<UnitConfig> unitConfigs = Registries.getUnitRegistry().getUnitConfigsByUnitType(UnitTemplate.UnitType.GATEWAY);

        filterIdsAlreadyListed(unitConfigs);
        filterIdsOfNotNestedGatewayClasses(unitConfigs);

        return unitConfigs;
    }

    private void filterIdsAlreadyListed(final List<UnitConfig> unitConfigs) throws CouldNotPerformException {
        final List<String> idsToFilter = new ArrayList<>();

        // get ids which are already nested
        final ValueListTreeItem valueListTreeItem = (ValueListTreeItem) getValueType().getTreeItem().getParent();
        for (final TreeItem child : (List<TreeItem>) valueListTreeItem.getChildren()) {
            final String id = ((ValueType<String>) child.getValue()).getValue();

            if (!id.isEmpty()) {
                idsToFilter.add(id);
            }
        }

        final ListFilter<UnitConfig> idFilter = unitConfig -> idsToFilter.contains(unitConfig.getId());
        idFilter.filter(unitConfigs);
    }

    private void filterIdsOfNotNestedGatewayClasses(final List<UnitConfig> unitConfigs) throws CouldNotPerformException {
        final UnitConfigTreeItem gatewayUnitTreeItem = (UnitConfigTreeItem) getValueType().getTreeItem().getParent().getParent().getParent();
        final GatewayClass gatewayClass = Registries.getClassRegistry().getGatewayClassById(gatewayUnitTreeItem.getBuilder().getGatewayConfig().getGatewayClassId());

        final ListFilter<UnitConfig> filterByGatewayClass = unitConfig -> gatewayClass.getNestedGatewayClassIdList().contains(unitConfig.getGatewayConfig().getGatewayClassId());
        filterByGatewayClass.filter(unitConfigs);
    }
}
