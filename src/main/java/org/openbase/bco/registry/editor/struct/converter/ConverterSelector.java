package org.openbase.bco.registry.editor.struct.converter;

/*
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
import com.google.protobuf.GeneratedMessage;
import org.openbase.bco.registry.editor.struct.converter.filter.AgentUnitConfigFilter;
import org.openbase.bco.registry.editor.struct.converter.filter.AppUnitConfigFilter;
import org.openbase.bco.registry.editor.struct.converter.filter.AuthorizationGroupUnitConfigFilter;
import org.openbase.bco.registry.editor.struct.converter.filter.ConnectionUnitConfigFilter;
import org.openbase.bco.registry.editor.struct.converter.filter.DalUnitConfigFilter;
import org.openbase.bco.registry.editor.struct.converter.filter.DeviceUnitConfigFilter;
import org.openbase.bco.registry.editor.struct.converter.filter.Filter;
import org.openbase.bco.registry.editor.struct.converter.filter.LocationUnitConfigFilter;
import org.openbase.bco.registry.editor.struct.converter.filter.SceneUnitConfigFilter;
import org.openbase.bco.registry.editor.struct.converter.filter.UnitGroupUnitConfigFilter;
import org.openbase.bco.registry.editor.struct.converter.filter.UserUnitConfigFilter;
import rst.domotic.state.EnablingStateType.EnablingState;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.geometry.RotationType.Rotation;
import rst.rsb.ScopeType.Scope;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ConverterSelector {

    public static Converter getConverter(GeneratedMessage.Builder builder) {
        if (builder instanceof Scope.Builder) {
            return new ScopeConverter((Scope.Builder) builder);
        } else if (builder instanceof Rotation.Builder) {
            return new RotationConverter((Rotation.Builder) builder);
        } else if (builder instanceof EnablingState.Builder) {
            return new EnablingStateConverter((EnablingState.Builder) builder);
        } else if (builder instanceof UnitConfig.Builder) {
            Filter filter;
            switch (((UnitConfig.Builder) builder).getType()) {
                case AGENT:
                    filter = new AgentUnitConfigFilter();
                    break;
                case APP:
                    filter = new AppUnitConfigFilter();
                    break;
                case AUTHORIZATION_GROUP:
                    filter = new AuthorizationGroupUnitConfigFilter();
                    break;
                case CONNECTION:
                    filter = new ConnectionUnitConfigFilter();
                    break;
                case DEVICE:
                    filter = new DeviceUnitConfigFilter();
                    break;
                case LOCATION:
                    filter = new LocationUnitConfigFilter();
                    break;
                case SCENE:
                    filter = new SceneUnitConfigFilter();
                    break;
                case UNIT_GROUP:
                    filter = new UnitGroupUnitConfigFilter();
                    break;
                case USER:
                    filter = new UserUnitConfigFilter();
                    break;
                default:
                    filter = new DalUnitConfigFilter();
                    break;
            }
            return new DefaultConverter(builder, filter);
        } else {
            return new DefaultConverter(builder);
        }
    }
}
