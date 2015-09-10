/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.struct.consistency;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import de.citec.csra.regedit.struct.GenericListContainer;
import de.citec.csra.regedit.struct.GenericNodeContainer;
import de.citec.csra.regedit.struct.Node;
import de.citec.csra.regedit.struct.NodeContainer;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
import de.citec.csra.regedit.util.RemotePool;
import de.citec.jul.exception.CouldNotPerformException;
import java.util.ArrayList;
import java.util.List;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.service.ServiceConfigType.ServiceConfig;
import rst.homeautomation.service.ServiceTemplateType.ServiceTemplate;
import rst.homeautomation.state.InventoryStateType.InventoryState;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.homeautomation.unit.UnitTemplateConfigType.UnitTemplateConfig;
import rst.person.PersonType;
import rst.timing.TimestampType.Timestamp;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class StructureConsistencyKeeper {

    public static void keepStructure(NodeContainer<? extends GeneratedMessage.Builder> container, String fieldName) throws CouldNotPerformException {
        if (container.getBuilder() instanceof InventoryState.Builder) {
            keepInventoryStateStructure((NodeContainer<InventoryState.Builder>) container);
        } else if (container.getBuilder() instanceof PersonType.Person.Builder && ((NodeContainer<GeneratedMessage.Builder>) container.getParent().getValue()).getBuilder() instanceof InventoryState.Builder) {
            keepInventoryStateStructure((NodeContainer<InventoryState.Builder>) container.getParent().getValue());
        } else if (container.getBuilder() instanceof DeviceConfig.Builder) {
            keepDeviceConfigStructure((NodeContainer<DeviceConfig.Builder>) container, fieldName);
        } else if (container.getBuilder() instanceof UnitTemplateConfig.Builder) {
            keepUnitTemplateConfigStructure((NodeContainer<UnitTemplateConfig.Builder>) container, fieldName);
        }
    }

    public static void clearField(NodeContainer<? extends GeneratedMessage.Builder> container, String fieldName) {
        GeneratedMessage.Builder builder = container.getBuilder();
        for (int i = 0; i < container.getChildren().size(); i++) {
            Node item = container.getChildren().get(i).getValue();
            if (item instanceof NodeContainer && ((NodeContainer) item).getDescriptor().equals(fieldName)) {
                container.getChildren().remove(i);
                i--;
            }
        }
        builder.clearField(FieldDescriptorUtil.getField(fieldName, builder));
    }

    private static void keepDeviceConfigStructure(NodeContainer<DeviceConfig.Builder> container, String changedField) throws CouldNotPerformException {
        if ("device_class_id".equals(changedField)) {
            // clear the field in the builder and remove all child tree items representing these
            StructureConsistencyKeeper.clearField(container, "unit_config");

            // create the new values for the field and add them to the builder  
            List<UnitConfig.Builder> builderList = new ArrayList<>();
            for (UnitTemplateConfig unitTemplate : RemotePool.getInstance().getDeviceRemote().getDeviceClassById(container.getBuilder().getDeviceClassId()).getUnitTemplateConfigList()) {
                UnitConfig.Builder unitConfig = UnitConfig.newBuilder().setType(unitTemplate.getType()).setBoundToDevice(true);
                unitConfig.setPlacementConfig(container.getBuilder().getPlacementConfig());
                unitTemplate.getServiceTemplateList().stream().forEach((serviceTemplate) -> {
                    unitConfig.addServiceConfig(ServiceConfig.newBuilder().setType(serviceTemplate.getServiceType()));
                });
                container.getBuilder().addUnitConfig(unitConfig);
                builderList.add(unitConfig);
            }

            // create and add a new child node container representing these children
            Descriptors.FieldDescriptor field = FieldDescriptorUtil.getField(DeviceConfig.UNIT_CONFIG_FIELD_NUMBER, container.getBuilder());
            container.add(new GenericListContainer(field.getName(), field, container.getBuilder(), builderList));
        }
    }

    private static void keepUnitTemplateConfigStructure(NodeContainer<UnitTemplateConfig.Builder> container, String changedField) throws CouldNotPerformException {
        // check if the right field has been set
        if ("type".equals(changedField)) {
            // clear the field in the builder and remove all child tree items representing these
            StructureConsistencyKeeper.clearField(container, "service_template");

            // create the new values for the field and add them to the builder  
            List<ServiceTemplate.Builder> builderList = new ArrayList<>();
            for (ServiceTemplate.ServiceType serviceType : RemotePool.getInstance().getDeviceRemote().getUnitTemplateByType(container.getBuilder().getType()).getServiceTypeList()) {
                ServiceTemplate.Builder serviceTemplateBuilder = ServiceTemplate.newBuilder().setServiceType(serviceType);
                container.getBuilder().addServiceTemplate(serviceTemplateBuilder);
                builderList.add(serviceTemplateBuilder);
            }

            // create and add a new child node container representing these children
            Descriptors.FieldDescriptor field = FieldDescriptorUtil.getField(UnitTemplateConfig.SERVICE_TEMPLATE_FIELD_NUMBER, container.getBuilder());
            container.add(new GenericListContainer(field.getName(), field, container.getBuilder(), builderList));
        }
    }

    private static void keepInventoryStateStructure(NodeContainer<InventoryState.Builder> container) throws CouldNotPerformException {
        // clear the field in the builder and remove all child tree items representing these
        StructureConsistencyKeeper.clearField(container, "timestamp");

        // create the new values for the field and add them to the builder  
        container.getBuilder().setTimestamp(Timestamp.newBuilder().setTime(System.currentTimeMillis()));

        // create and add a new child node container representing these children
        Descriptors.FieldDescriptor field = FieldDescriptorUtil.getField(InventoryState.TIMESTAMP_FIELD_NUMBER, container.getBuilder());
        container.add(new GenericNodeContainer<>(field, container.getBuilder().getTimestampBuilder()));
    }

    public static void keepStructure(GeneratedMessage.Builder builder, String fieldName) throws CouldNotPerformException {
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
}
