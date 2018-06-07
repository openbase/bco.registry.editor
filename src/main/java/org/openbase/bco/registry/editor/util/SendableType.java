package org.openbase.bco.registry.editor.util;

/*
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
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import rst.domotic.activity.ActivityConfigType.ActivityConfig;
import rst.domotic.activity.ActivityTemplateType.ActivityTemplate;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import rst.domotic.unit.agent.AgentClassType.AgentClass;
import rst.domotic.unit.app.AppClassType.AppClass;
import rst.domotic.unit.device.DeviceClassType.DeviceClass;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public enum SendableType {

    AGENT_CONFIG(UnitConfig.newBuilder().setUnitType(UnitType.AGENT).build()),
    AGENT_CLASS(AgentClass.getDefaultInstance()),
    APP_CONFIG(UnitConfig.newBuilder().setUnitType(UnitType.APP).build()),
    APP_CLASS(AppClass.getDefaultInstance()),
    DEVICE_CLASS(DeviceClass.getDefaultInstance()),
    DEVICE_CONFIG(UnitConfig.newBuilder().setUnitType(UnitType.DEVICE).build()),
    LOCATION_CONFIG(UnitConfig.newBuilder().setUnitType(UnitType.LOCATION).build()),
    CONNECTION_CONFIG(UnitConfig.newBuilder().setUnitType(UnitType.CONNECTION).build()),
    SCENE_CONFIG(UnitConfig.newBuilder().setUnitType(UnitType.SCENE).build()),
    UNIT_TEMPLATE(UnitTemplate.getDefaultInstance()),
    USER_CONFIG(UnitConfig.newBuilder().setUnitType(UnitType.USER).build()),
    AUTHORIZATION_GROUP_CONFIG(UnitConfig.newBuilder().setUnitType(UnitType.AUTHORIZATION_GROUP).build()),
    UNIT_CONFIG(UnitConfig.newBuilder().setUnitType(UnitType.UNKNOWN).build()),
    UNIT_GROUP_CONFIG(UnitConfig.newBuilder().setUnitType(UnitType.UNIT_GROUP).build()),
    SERVICE_TEMPLATE(ServiceTemplate.getDefaultInstance()),
    ACTIVITY_TEMPLATE(ActivityTemplate.getDefaultInstance()),
    ACTIVITY_CONFIG(ActivityConfig.getDefaultInstance());

    private final GeneratedMessage defaultInstanceForType;

    SendableType(GeneratedMessage message) {
        defaultInstanceForType = message;
    }

    public GeneratedMessage getDefaultInstanceForType() {
        return defaultInstanceForType;
    }

    public static SendableType getTypeToBuilder(Message.Builder builder) {
        if (builder instanceof AgentClass.Builder) {
            return AGENT_CLASS;
        } else if (builder instanceof AppClass.Builder) {
            return APP_CLASS;
        } else if (builder instanceof DeviceClass.Builder) {
            return DEVICE_CLASS;
        } else if (builder instanceof UnitTemplate.Builder) {
            return UNIT_TEMPLATE;
        } else if (builder instanceof ServiceTemplate.Builder) {
            return SERVICE_TEMPLATE;
        } else if (builder instanceof UnitConfig.Builder) {
            switch (((UnitConfig.Builder) builder).getUnitType()) {
                case AGENT:
                    return AGENT_CONFIG;
                case APP:
                    return APP_CONFIG;
                case DEVICE:
                    return DEVICE_CONFIG;
                case LOCATION:
                    return LOCATION_CONFIG;
                case CONNECTION:
                    return CONNECTION_CONFIG;
                case SCENE:
                    return SCENE_CONFIG;
                case USER:
                    return USER_CONFIG;
                case AUTHORIZATION_GROUP:
                    return AUTHORIZATION_GROUP_CONFIG;
                case UNIT_GROUP:
                    return UNIT_GROUP_CONFIG;
                default:
                    return UNIT_CONFIG;
            }
        } else if (builder instanceof ActivityTemplate.Builder) {
            return ACTIVITY_TEMPLATE;
        } else if (builder instanceof ActivityConfig.Builder) {
            return ACTIVITY_CONFIG;
        } else {
            return null;
        }
    }

    public static SendableType getTypeToMessage(Message msg) {
        return getTypeToBuilder(msg.toBuilder());
    }
}
