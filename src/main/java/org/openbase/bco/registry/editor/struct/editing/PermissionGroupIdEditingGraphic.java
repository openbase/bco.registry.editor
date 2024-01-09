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
import org.openbase.bco.registry.editor.struct.BuilderListTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.type.domotic.authentication.PermissionConfigType.PermissionConfig;
import org.openbase.type.domotic.authentication.PermissionConfigType.PermissionConfig.MapFieldEntry;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class PermissionGroupIdEditingGraphic extends AbstractMessageEditingGraphic<UnitConfig> {

    public PermissionGroupIdEditingGraphic(final ValueType<String> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(value -> {
            try {
                return LabelProcessor.getBestMatch(value.getLabel());
            } catch (CouldNotPerformException e) {
                return value.getId();
            }
        }, valueType, treeTableCell);
    }

    @Override
    protected List<UnitConfig> getMessages() throws CouldNotPerformException {
        // get first parent having access to the whole permission config
        BuilderListTreeItem<PermissionConfig.Builder> parent = (BuilderListTreeItem<PermissionConfig.Builder>) getValueType().getTreeItem().getParent().getParent();

        // get all authorization groups
        final List<UnitConfig> unitConfigs = Registries.getUnitRegistry().getUnitConfigsByUnitType(UnitType.AUTHORIZATION_GROUP);
        logger.info(unitConfigs.size() + " groups");
        // filter all group ids which already have entries defined
        for (final MapFieldEntry mapFieldEntry : parent.getBuilder().getGroupPermissionList()) {
            // do not remove the value of the current group
            if (mapFieldEntry.getGroupId().equals(getValueType().getValue())) {
                continue;
            }
            logger.info("Remove: " + mapFieldEntry.getGroupId());
            unitConfigs.remove(Registries.getUnitRegistry().getUnitConfigById(mapFieldEntry.getGroupId()));
        }
        logger.info(unitConfigs.size() + " groups");
        return unitConfigs;
    }

    @Override
    protected String getCurrentValue(final UnitConfig message) {
        return message.getId();
    }

    @Override
    protected UnitConfig getMessage(final String value) throws CouldNotPerformException {
        return Registries.getUnitRegistry().getUnitConfigById(value);
    }
}
