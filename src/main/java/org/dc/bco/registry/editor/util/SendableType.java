package org.dc.bco.registry.editor.util;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 DivineCooperation
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
import com.google.protobuf.Message;
import rst.authorization.UserConfigType.UserConfig;
import rst.authorization.UserGroupConfigType.UserGroupConfig;
import rst.homeautomation.control.agent.AgentConfigType.AgentConfig;
import rst.homeautomation.control.app.AppConfigType.AppConfig;
import rst.homeautomation.control.scene.SceneConfigType.SceneConfig;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.unit.UnitGroupConfigType.UnitGroupConfig;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate;
import rst.spatial.ConnectionConfigType.ConnectionConfig;
import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public enum SendableType {

    AGENT_CONFIG(AgentConfig.getDefaultInstance()),
    APP_CONFIG(AppConfig.getDefaultInstance()),
    DEVICE_CLASS(DeviceClass.getDefaultInstance()),
    DEVICE_CONFIG(DeviceConfig.getDefaultInstance()),
    LOCATION_CONFIG(LocationConfig.getDefaultInstance()),
    CONNECTION_CONFIG(ConnectionConfig.getDefaultInstance()),
    SCENE_CONFIG(SceneConfig.getDefaultInstance()),
    UNIT_TEMPLATE(UnitTemplate.getDefaultInstance()),
    USER_CONFIG(UserConfig.getDefaultInstance()),
    USER_GROUP_CONFIG(UserGroupConfig.getDefaultInstance()),
    UNIT_GROUP_CONFIG(UnitGroupConfig.getDefaultInstance());

    private final GeneratedMessage defaultInstanceForType;

    private SendableType(GeneratedMessage message) {
        defaultInstanceForType = message;
    }

    public GeneratedMessage getDefaultInstanceForType() {
        return defaultInstanceForType;
    }

    public static SendableType getTypeToBuilder(Message.Builder builder) {
        if (builder instanceof AgentConfig.Builder) {
            return AGENT_CONFIG;
        } else if (builder instanceof AppConfig.Builder) {
            return APP_CONFIG;
        } else if (builder instanceof DeviceClass.Builder) {
            return DEVICE_CLASS;
        } else if (builder instanceof DeviceConfig.Builder) {
            return DEVICE_CONFIG;
        } else if (builder instanceof LocationConfig.Builder) {
            return LOCATION_CONFIG;
        } else if (builder instanceof ConnectionConfig.Builder) {
            return CONNECTION_CONFIG;
        } else if (builder instanceof SceneConfig.Builder) {
            return SCENE_CONFIG;
        } else if (builder instanceof UnitTemplate.Builder) {
            return UNIT_TEMPLATE;
        } else if (builder instanceof UserConfig.Builder) {
            return USER_CONFIG;
        } else if (builder instanceof UserGroupConfig.Builder) {
            return USER_GROUP_CONFIG;
        } else if (builder instanceof UnitGroupConfig.Builder) {
            return UNIT_GROUP_CONFIG;
        } else {
            return null;
        }
    }

    public static SendableType getTypeToMessage(Message msg) {
        return getTypeToBuilder(msg.toBuilder());
    }
}
