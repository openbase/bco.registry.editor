package org.openbase.bco.registry.editor.struct.consistency;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 openbase.org
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
import rst.domotic.registry.DeviceRegistryDataType.DeviceRegistryData;
import rst.domotic.service.ServiceConfigType.ServiceConfig;
import rst.domotic.service.ServiceTemplateConfigType.ServiceTemplateConfig;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateConfigType.UnitTemplateConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate;
import rst.domotic.unit.agent.AgentClassType.AgentClass;
import rst.domotic.unit.app.AppClassType.AppClass;
import rst.domotic.unit.device.DeviceClassType.DeviceClass;
import rst.domotic.unit.device.DeviceConfigType.DeviceConfig;
import rst.domotic.unit.location.LocationConfigType.LocationConfig;
import rst.rsb.ScopeType.Scope;
import rst.spatial.PlacementConfigType.PlacementConfig;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class Configuration {

    //TODO:tamino redesign
    public static boolean isModifiableList(GeneratedMessage.Builder builder, String fieldName) {
        if (builder instanceof UnitTemplateConfig.Builder && "service_template_config".equals(fieldName)) {
            return false;
        } else if (builder instanceof DeviceConfig.Builder && "unit_id".equals(fieldName)) {
            return false;
        } else if (builder instanceof UnitConfig.Builder && "service_config".equals(fieldName)) {
            return false;
        } else if (builder instanceof DeviceRegistryData.Builder && "unit_template".equals(fieldName)) {
            return false;
        } else if (builder instanceof LocationConfig.Builder && "unit_id".equals(fieldName)) {
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
            return !(("id".equals(fieldName)) || "unit_id".equals(fieldName));
        } else if (builder instanceof Scope.Builder) {
            return !("scope".equals(fieldName));
        } else if (builder instanceof UnitConfig.Builder) {
            if (((UnitConfig.Builder) builder).getType() == UnitTemplate.UnitType.USER && "label".equals(fieldName)) {
                return false;
            }
            return !("id".equals(fieldName) || "unit_host_id".equals(fieldName) || "type".equals(fieldName) || "unit_template_config_id".equals(fieldName));
        } else if (builder instanceof ServiceConfig.Builder) {
            return !("type".equals(fieldName) || "unit_id".equals(fieldName));
        } else if (builder instanceof LocationConfig.Builder) {
            return !("id".equals(fieldName) || "root".equals(fieldName) || "unit_id".equals(fieldName));
        } else if (builder instanceof ServiceTemplateConfig.Builder) {
            return !("type".equals(fieldName));
        } else if (builder instanceof PlacementConfig.Builder) {
            return !("transformation_frame_id".equals(fieldName));
        }
        return true;
    }

    public static boolean isSendable(Message.Builder builder) {
        return ((builder instanceof DeviceClass.Builder)
                || (builder instanceof UnitTemplate.Builder)
                || (builder instanceof AgentClass.Builder)
                || (builder instanceof AppClass.Builder)
                || (builder instanceof UnitConfig.Builder));
    }
}
