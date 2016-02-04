package org.dc.bco.registry.editor.struct.consistency;

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
import rst.homeautomation.device.DeviceRegistryType.DeviceRegistry;
import rst.homeautomation.service.ServiceConfigType.ServiceConfig;
import rst.homeautomation.service.ServiceTemplateType.ServiceTemplate;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.homeautomation.unit.UnitGroupConfigType.UnitGroupConfig;
import rst.homeautomation.unit.UnitTemplateConfigType.UnitTemplateConfig;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate;
import rst.rsb.ScopeType.Scope;
import rst.spatial.ConnectionConfigType.ConnectionConfig;
import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class Configuration {

    //TODO:tamino redesign
    public static boolean isModifiableList(GeneratedMessage.Builder builder, String fieldName) {
        if (builder instanceof UnitTemplateConfig.Builder && "service_template".equals(fieldName)) {
            return false;
        } else if (builder instanceof DeviceConfig.Builder && "unit_config".equals(fieldName)) {
            return false;
        } else if (builder instanceof UnitConfig.Builder && "service_config".equals(fieldName)) {
            return false;
        } else if (builder instanceof DeviceRegistry.Builder && "unit_template".equals(fieldName)) {
            return false;
        } else if (builder instanceof LocationConfig.Builder && "unit_id".equals(fieldName)) {
            return false;
        } else if (builder instanceof ConnectionConfig.Builder && "unit_id".equals(fieldName)) {
            return false;
        }
        return true;
    }

    public static boolean isModifiableField(GeneratedMessage.Builder builder, String fieldName) {
        if (builder instanceof UnitTemplate.Builder) {
            return !("type".equals(fieldName) || "id".equals(fieldName));
        } else if (builder instanceof DeviceClass.Builder) {
            if ("product_number".equals(fieldName)) {
                return ((DeviceClass.Builder) builder).getProductNumber().isEmpty();
            }
            return !("id".equals(fieldName));
        } else if (builder instanceof ServiceTemplate.Builder) {
            return !("service_type".equals(fieldName));
        } else if (builder instanceof DeviceConfig.Builder) {
            if ("serial_number".equals(fieldName)) {
                return ((DeviceConfig.Builder) builder).getSerialNumber().isEmpty();
            } else if ("device_class_id".equals(fieldName)) {
                return ((DeviceConfig.Builder) builder).getDeviceClassId().isEmpty();
            }
            return !("id".equals(fieldName));
        } else if (builder instanceof Scope.Builder) {
            return !("scope".equals(fieldName));
        } else if (builder instanceof UnitConfig.Builder) {
            return !("id".equals(fieldName) || "device_id".equals(fieldName) || "type".equals(fieldName) || "unit_template_config_id".equals(fieldName));
        } else if (builder instanceof ServiceConfig.Builder) {
            return !("type".equals(fieldName) || "unit_id".equals(fieldName));
        } else if (builder instanceof LocationConfig.Builder) {
            return !("id".equals(fieldName) || "root".equals(fieldName) || "unit_id".equals(fieldName));
        } else if (builder instanceof ConnectionConfig.Builder) {
            return !("id".equals(fieldName));
        } else if (builder instanceof SceneConfig.Builder) {
            return !("id".equals(fieldName));
        } else if (builder instanceof AgentConfig.Builder) {
            return !("id".equals(fieldName));
        } else if (builder instanceof AppConfig.Builder) {
            return !("id".equals(fieldName));
        } else if (builder instanceof UnitTemplateConfig.Builder) {
            return !("id".equals(fieldName));
        } else if (builder instanceof UserConfig.Builder) {
            return !("id".equals(fieldName));
        } else if (builder instanceof UserGroupConfig.Builder) {
            return !("id".equals(fieldName));
        } else if (builder instanceof UnitGroupConfig.Builder) {
            return !("id".equals(fieldName));
        }
        return true;
    }

    public static boolean isSendable(Message.Builder builder) {
        return ((builder instanceof DeviceClass.Builder)
                || (builder instanceof DeviceConfig.Builder)
                || (builder instanceof UnitTemplate.Builder)
                || (builder instanceof LocationConfig.Builder)
                || (builder instanceof AgentConfig.Builder)
                || (builder instanceof AppConfig.Builder)
                || (builder instanceof SceneConfig.Builder)
                || (builder instanceof ConnectionConfig.Builder)
                || (builder instanceof UserConfig.Builder)
                || (builder instanceof UserGroupConfig.Builder)
                || (builder instanceof UnitGroupConfig.Builder));
    }
}
