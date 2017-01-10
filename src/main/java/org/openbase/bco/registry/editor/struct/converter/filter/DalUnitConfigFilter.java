package org.openbase.bco.registry.editor.struct.converter.filter;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2017 openbase.org
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

import rst.domotic.unit.UnitConfigType.UnitConfig;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DalUnitConfigFilter extends UnitConfigFilter {

    @Override
    protected void registerFilteredFields() {
        registerFilteredField(
                UnitConfig.AGENT_CONFIG_FIELD_NUMBER,
                UnitConfig.APP_CONFIG_FIELD_NUMBER,
                UnitConfig.AUTHORIZATION_GROUP_CONFIG_FIELD_NUMBER,
                UnitConfig.CONNECTION_CONFIG_FIELD_NUMBER,
                UnitConfig.DEVICE_CONFIG_FIELD_NUMBER,
                UnitConfig.LOCATION_CONFIG_FIELD_NUMBER,
                UnitConfig.SCENE_CONFIG_FIELD_NUMBER,
                UnitConfig.UNIT_GROUP_CONFIG_FIELD_NUMBER,
                UnitConfig.USER_CONFIG_FIELD_NUMBER);
    }

}
