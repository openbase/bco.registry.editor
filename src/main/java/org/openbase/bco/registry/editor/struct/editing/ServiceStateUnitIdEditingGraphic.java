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
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.preset.ServiceStateDescriptionTreeItem;
import org.openbase.bco.registry.editor.struct.preset.UnitConfigTreeItem;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.List;

/**
 * Special message editing graphic for the unit id in a service state description.
 * Unit configs are displayed by their scope.
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ServiceStateUnitIdEditingGraphic extends AbstractMessageEditingGraphic<UnitConfig> {

    /**
     * {@inheritDoc}
     *
     * @param valueType     {@inheritDoc}
     * @param treeTableCell {@inheritDoc}
     */
    public ServiceStateUnitIdEditingGraphic(final ValueType<String> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(unitConfig -> {
            try {
                return ScopeGenerator.generateStringRep(unitConfig.getScope());
            } catch (CouldNotPerformException e) {
                return unitConfig.getId();
            }
        }, valueType, treeTableCell);
    }

    /**
     * {@inheritDoc}
     *
     * @return a list of all units configs having the same unit type as defined in the service state description and
     * being located inside the same location as the scene unit config.
     *
     * @throws CouldNotPerformException {@inheritDoc}
     */
    @Override
    protected List<UnitConfig> getMessages() throws CouldNotPerformException {
        final ServiceStateDescriptionTreeItem serviceStateDescriptionTreeItem = (ServiceStateDescriptionTreeItem) getValueType().getTreeItem().getParent();
        final UnitType unitType = serviceStateDescriptionTreeItem.getBuilder().getUnitType();
        if (serviceStateDescriptionTreeItem.getParent().getParent().getParent() instanceof UnitConfigTreeItem) {
            final UnitConfigTreeItem unitConfigTreeItem = (UnitConfigTreeItem) serviceStateDescriptionTreeItem.getParent().getParent().getParent();
            final String locationId = unitConfigTreeItem.getBuilder().getPlacementConfig().getLocationId();
            if (!locationId.isEmpty()) {
                // resolve all unit inside of same location as unit
                final List<UnitConfig> unitConfigsByLocation = Registries.getUnitRegistry().getUnitConfigsByLocation(unitType, locationId);
                // also add the same location of the unit
                unitConfigsByLocation.add(Registries.getUnitRegistry().getUnitConfigById(locationId));
                return unitConfigsByLocation;
            }
        }
        return Registries.getUnitRegistry().getUnitConfigs(unitType);
    }

    /**
     * {@inheritDoc}
     *
     * @param message {@inheritDoc}
     *
     * @return the id of the selected unit config.
     */
    @Override
    protected String getCurrentValue(final UnitConfig message) {
        return message.getId();
    }

    /**
     * {@inheritDoc}
     *
     * @param unitId the current unit id.
     *
     * @return the unit config with the given id.
     *
     * @throws CouldNotPerformException {@inheritDoc}
     */
    @Override
    protected UnitConfig getMessage(final String unitId) throws CouldNotPerformException {
        return Registries.getUnitRegistry().getUnitConfigById(unitId);
    }
}
