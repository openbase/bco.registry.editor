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

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import java.util.ArrayList;
import java.util.List;
import org.dc.bco.registry.editor.struct.GenericListContainer;
import org.dc.bco.registry.editor.struct.GenericNodeContainer;
import org.dc.bco.registry.editor.struct.LeafContainer;
import org.dc.bco.registry.editor.struct.Node;
import org.dc.bco.registry.editor.struct.NodeContainer;
import org.dc.bco.registry.editor.util.FieldDescriptorUtil;
import org.dc.bco.registry.editor.util.RemotePool;
import org.dc.jul.exception.CouldNotPerformException;
import org.slf4j.LoggerFactory;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.service.ServiceConfigType.ServiceConfig;
import rst.homeautomation.service.ServiceTemplateType.ServiceTemplate;
import rst.homeautomation.state.InventoryStateType.InventoryState;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.homeautomation.unit.UnitGroupConfigType.UnitGroupConfig;
import rst.homeautomation.unit.UnitTemplateConfigType.UnitTemplateConfig;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate.UnitType;
import rst.person.PersonType;
import rst.timing.TimestampType.Timestamp;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class StructureConsistencyKeeper {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(StructureConsistencyKeeper.class);

    public static void keepStructure(NodeContainer<? extends Message.Builder> container, String fieldName) throws CouldNotPerformException {
        if (container.getBuilder() instanceof InventoryState.Builder) {
            keepInventoryStateStructure((NodeContainer<InventoryState.Builder>) container);
        } else if (container.getBuilder() instanceof PersonType.Person.Builder && ((NodeContainer<GeneratedMessage.Builder>) container.getParent().getValue()).getBuilder() instanceof InventoryState.Builder) {
            keepInventoryStateStructure((NodeContainer<InventoryState.Builder>) container.getParent().getValue());
        } else if (container.getBuilder() instanceof DeviceConfig.Builder) {
            keepDeviceConfigStructure((NodeContainer<DeviceConfig.Builder>) container, fieldName);
        } else if (container.getBuilder() instanceof UnitTemplateConfig.Builder) {
            keepUnitTemplateConfigStructure((NodeContainer<UnitTemplateConfig.Builder>) container, fieldName);
        } else if (container.getBuilder() instanceof UnitGroupConfig.Builder) {
            if (container instanceof GenericListContainer) {
                keepUnitGroupConfigStructure(((GenericNodeContainer<UnitGroupConfig.Builder>) container.getParent().getValue()), fieldName);
            } else {
                keepUnitGroupConfigStructure(((GenericNodeContainer<UnitGroupConfig.Builder>) container), fieldName);
            }
        }
    }

    public static void clearField(NodeContainer<? extends Message.Builder> container, String fieldName) {
        GeneratedMessage.Builder builder = container.getBuilder();
        for (int i = 0; i < container.getChildren().size(); i++) {
            Node item = container.getChildren().get(i).getValue();
            if (item instanceof NodeContainer && ((NodeContainer) item).getDescriptor().equals(fieldName)) {
                container.getChildren().remove(i);
                i--;
            } else if (item instanceof LeafContainer && ((LeafContainer) item).getDescriptor().equals(fieldName)) {
                container.getChildren().remove(i);
                i--;
            }
        }
        builder.clearField(FieldDescriptorUtil.getFieldDescriptor(fieldName, builder));
    }

    private static void keepDeviceConfigStructure(NodeContainer<DeviceConfig.Builder> container, String changedField) throws CouldNotPerformException {
        if ("device_class_id".equals(changedField)) {
            // clear the field in the builder and remove all child tree items representing these
            StructureConsistencyKeeper.clearField(container, "unit_config");

            // create the new values for the field and add them to the builder
            for (UnitTemplateConfig unitTemplate : RemotePool.getInstance().getDeviceRemote().getDeviceClassById(container.getBuilder().getDeviceClassId()).getUnitTemplateConfigList()) {
                UnitConfig.Builder unitConfig = UnitConfig.newBuilder().setType(unitTemplate.getType()).setBoundToDevice(true);
                unitConfig.setPlacementConfig(container.getBuilder().getPlacementConfig());
                unitTemplate.getServiceTemplateList().stream().forEach((serviceTemplate) -> {
                    unitConfig.addServiceConfig(ServiceConfig.newBuilder().setType(serviceTemplate.getServiceType()));
                });
                container.getBuilder().addUnitConfig(unitConfig);
            }

            // create and add a new child node container representing these children
            Descriptors.FieldDescriptor field = FieldDescriptorUtil.getFieldDescriptor(DeviceConfig.UNIT_CONFIG_FIELD_NUMBER, container.getBuilder());
            container.add(new GenericListContainer(field, container.getBuilder()));
        }
    }

    private static void keepUnitTemplateConfigStructure(NodeContainer<UnitTemplateConfig.Builder> container, String changedField) throws CouldNotPerformException {
        // check if the right field has been set
        if ("type".equals(changedField)) {
            // clear the field in the builder and remove all child tree items representing these
            StructureConsistencyKeeper.clearField(container, "service_template");

            // create the new values for the field and add them to the builder
            for (ServiceTemplate.ServiceType serviceType : RemotePool.getInstance().getDeviceRemote().getUnitTemplateByType(container.getBuilder().getType()).getServiceTypeList()) {
                ServiceTemplate.Builder serviceTemplateBuilder = ServiceTemplate.newBuilder().setServiceType(serviceType);
                container.getBuilder().addServiceTemplate(serviceTemplateBuilder);
            }

            // create and add a new child node container representing these children
            Descriptors.FieldDescriptor field = FieldDescriptorUtil.getFieldDescriptor(UnitTemplateConfig.SERVICE_TEMPLATE_FIELD_NUMBER, container.getBuilder());
            container.add(new GenericListContainer(field, container.getBuilder()));
        }
    }

    private static void keepInventoryStateStructure(NodeContainer<InventoryState.Builder> container) throws CouldNotPerformException {
        // clear the field in the builder and remove all child tree items representing these
        StructureConsistencyKeeper.clearField(container, "timestamp");

        // create the new values for the field and add them to the builder
        container.getBuilder().setTimestamp(Timestamp.newBuilder().setTime(System.currentTimeMillis()));

        // create and add a new child node container representing these children
        Descriptors.FieldDescriptor field = FieldDescriptorUtil.getFieldDescriptor(InventoryState.TIMESTAMP_FIELD_NUMBER, container.getBuilder());
        container.add(new GenericNodeContainer<>(field, container.getBuilder().getTimestampBuilder()));
    }

    public static void keepStructure(Message.Builder builder, String fieldName) throws CouldNotPerformException {
        if (builder instanceof DeviceConfig.Builder) {
            keepDeviceConfigStructure((DeviceConfig.Builder) builder, fieldName);
        }
    }

    private static void keepDeviceConfigStructure(DeviceConfig.Builder builder, String changedField) throws CouldNotPerformException {
        if ("device_class_id".equals(changedField)) {

            for (UnitTemplateConfig unitTemplate : RemotePool.getInstance().getDeviceRemote().getDeviceClassById(builder.getDeviceClassId()).getUnitTemplateConfigList()) {
                UnitConfig.Builder unitConfig = UnitConfig.newBuilder().setType(unitTemplate.getType()).setBoundToDevice(true);
                unitConfig.setPlacementConfig(builder.getPlacementConfig());
                unitTemplate.getServiceTemplateList().stream().forEach((serviceTemplate) -> {
                    unitConfig.addServiceConfig(ServiceConfig.newBuilder().setType(serviceTemplate.getServiceType()));
                });
                builder.addUnitConfig(unitConfig);
            }
        }
    }

    //unit group configs ... if service type changes set unit type unknown,
    //                       if unit type changes set service types,
    //                       in all cases remove incongrous member ids
    private static void keepUnitGroupConfigStructure(GenericNodeContainer<UnitGroupConfig.Builder> container, String changedField) throws CouldNotPerformException {
        Descriptors.FieldDescriptor field;
        boolean change = false;
        if ("service_type".equals(changedField)) {
            change = true;
            StructureConsistencyKeeper.clearField(container, "unit_type");
            container.getBuilder().setUnitType(UnitType.UNKNOWN);
            field = FieldDescriptorUtil.getFieldDescriptor(UnitGroupConfig.UNIT_TYPE_FIELD_NUMBER, container.getBuilder());
            container.registerElement(field.getName(), container.getBuilder().getField(field));
        } else if ("unit_type".equals(changedField)) {
            change = true;
            StructureConsistencyKeeper.clearField(container, "service_type");
            container.getBuilder().addAllServiceType(RemotePool.getInstance().getDeviceRemote().getUnitTemplateByType(container.getBuilder().getUnitType()).getServiceTypeList());
            field = FieldDescriptorUtil.getFieldDescriptor(UnitGroupConfig.SERVICE_TYPE_FIELD_NUMBER, container.getBuilder());
            container.add(new GenericListContainer<>(field, container.getBuilder()));
        }

        if (change) {
            List<String> memberIds = new ArrayList<>();
            if (container.getBuilder().getUnitType() == UnitType.UNKNOWN) {
                //check if every unit hast all given services
                for (String memberId : container.getBuilder().getMemberIdList()) {
                    UnitConfig unitConfig = RemotePool.getInstance().getDeviceRemote().getUnitConfigById(memberId);
                    boolean skip = false;
                    for (ServiceConfig serviceConfig : unitConfig.getServiceConfigList()) {
                        if (!container.getBuilder().getServiceTypeList().contains(serviceConfig.getType())) {
                            skip = true;
                        }
                    }
                    if (skip) {
                        continue;
                    }
                    memberIds.add(memberId);
                }
            } else {
                for (String memberId : container.getBuilder().getMemberIdList()) {
                    UnitType unitType = RemotePool.getInstance().getDeviceRemote().getUnitConfigById(memberId).getType();
                    if (unitType == container.getBuilder().getUnitType() || RemotePool.getInstance().getDeviceRemote().getSubUnitTypesOfUnitType(container.getBuilder().getUnitType()).contains(unitType)) {
                        memberIds.add(memberId);
                    }
                }
            }
            StructureConsistencyKeeper.clearField(container, "member_id");
            container.getBuilder().addAllMemberId(memberIds);
            field = FieldDescriptorUtil.getFieldDescriptor(UnitGroupConfig.MEMBER_ID_FIELD_NUMBER, container.getBuilder());
            container.add(new GenericListContainer<>(field, container.getBuilder()));
        }
    }
}
