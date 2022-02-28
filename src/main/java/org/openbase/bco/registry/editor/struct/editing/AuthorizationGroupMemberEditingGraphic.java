package org.openbase.bco.registry.editor.struct.editing;

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

import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.AbstractBuilderTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import org.openbase.type.domotic.unit.authorizationgroup.AuthorizationGroupConfigType.AuthorizationGroupConfig;

import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class AuthorizationGroupMemberEditingGraphic extends AbstractUnitConfigEditingGraphic {

    public AuthorizationGroupMemberEditingGraphic(final ValueType<String> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(unitConfig -> unitConfig.getUserConfig().getUserName(), valueType, treeTableCell);
    }

    @Override
    protected List<UnitConfig> getMessages() throws CouldNotPerformException {
        final List<UnitConfig> unitConfigs = Registries.getUnitRegistry().getUnitConfigsByUnitType(UnitType.USER);

        // get the current authorization group config
        final AuthorizationGroupConfig.Builder config = (AuthorizationGroupConfig.Builder) ((AbstractBuilderTreeItem) getValueType().getTreeItem().getParent()).getBuilder();

        // remove all members already contained
        for (String memberId : config.getMemberIdList()) {
            if (memberId.equals(getValueType().getValue())) {
                continue;
            }

            unitConfigs.remove(Registries.getUnitRegistry().getUnitConfigById(memberId));
        }

        return unitConfigs;
    }
}
