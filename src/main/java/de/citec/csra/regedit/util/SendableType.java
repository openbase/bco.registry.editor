/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.util;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import rst.authorization.UserConfigType.UserConfig;
import rst.authorization.UserGroupConfigType.UserGroupConfig;
import rst.homeautomation.control.agent.AgentConfigType.AgentConfig;
import rst.homeautomation.control.app.AppConfigType.AppConfig;
import rst.homeautomation.control.scene.SceneConfigType.SceneConfig;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate;
import rst.spatial.LocationConfigType.LocationConfig;
import rst.spatial.ConnectionConfigType.ConnectionConfig;

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
    GROUP_CONFIG(UserGroupConfig.getDefaultInstance());

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
            return GROUP_CONFIG;
        } else {
            return null;
        }
    }

    public static SendableType getTypeToMessage(Message msg) {
        return getTypeToBuilder(msg.toBuilder());
    }
}
