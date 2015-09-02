/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.util;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import de.citec.csra.regedit.struct.GenericListContainer;
import de.citec.csra.regedit.struct.GenericNodeContainer;
import de.citec.csra.regedit.struct.Node;
import de.citec.csra.regedit.struct.NodeContainer;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
import de.citec.csra.regedit.util.RemotePool;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.ExceptionPrinter;
import de.citec.jul.exception.InstantiationException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.control.agent.AgentConfigType;
import rst.homeautomation.control.app.AppConfigType;
import rst.homeautomation.control.scene.SceneConfigType;
import rst.homeautomation.device.DeviceClassType;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.device.DeviceRegistryType;
import rst.homeautomation.service.ServiceConfigType;
import rst.homeautomation.service.ServiceTemplateType.ServiceTemplate;
import rst.homeautomation.state.InventoryStateType.InventoryState;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.homeautomation.unit.UnitTemplateConfigType.UnitTemplateConfig;
import rst.homeautomation.unit.UnitTemplateType;
import rst.rsb.ScopeType;
import rst.spatial.LocationConfigType;
import rst.timing.TimestampType.Timestamp;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class Configuration {

    private final RemotePool remotePool;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Configuration() throws InstantiationException {
        this.remotePool = RemotePool.getInstance();
    }

    public static void adjust(NodeContainer container, String fieldName) throws InstantiationException {
        if (container.getBuilder() instanceof InventoryState.Builder) {
            adjustInventoryState((NodeContainer<InventoryState.Builder>) container);
        } else if (container.getBuilder() instanceof UnitTemplateConfig.Builder) {

        } else if (container.getBuilder() instanceof DeviceConfig.Builder) {

        }
    }

    private static void adjustInventoryState(NodeContainer<InventoryState.Builder> container) throws InstantiationException {
        // clear the field in the builder and remove all child tree items representing these
        clearField(container, "timestamp");

        // create the new values for the field and add them to the builder  
        container.getBuilder().setTimestamp(Timestamp.newBuilder().setTime(System.currentTimeMillis()));

        // create and add a new child node container representing these children
        Descriptors.FieldDescriptor field = FieldDescriptorUtil.getField(InventoryState.TIMESTAMP_FIELD_NUMBER, container.getBuilder());
        container.add(new GenericNodeContainer<>(field, container.getBuilder().getTimestampBuilder()));
    }

    public void adjustUnitTemplateConfig(NodeContainer<UnitTemplateConfig.Builder> container, String changedField) {
        // check if the right field has been set
        if ("type".equals(changedField)) {
            try {
                // clear the field in the builder and remove all child tree items representing these
                clearField(container, "unit_config");

                // create the new values for the field and add them to the builder  
                List<ServiceTemplate.Builder> builderList = new ArrayList<>();
                for (ServiceTemplate.ServiceType serviceType : remotePool.getDeviceRemote().getUnitTemplateByType(container.getBuilder().getType()).getServiceTypeList()) {
                    ServiceTemplate.Builder serviceTemplateBuilder = ServiceTemplate.newBuilder().setServiceType(serviceType);
                    container.getBuilder().addServiceTemplate(serviceTemplateBuilder);
                    builderList.add(serviceTemplateBuilder);
                }

                // create and add a new child node container representing these children
                Descriptors.FieldDescriptor field = FieldDescriptorUtil.getField(UnitTemplateConfig.SERVICE_TEMPLATE_FIELD_NUMBER, container.getBuilder());
                container.add(new GenericListContainer(field.getName(), field, container.getBuilder(), builderList));
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory(logger, ex);
            }
        }
    }

    public void adjustDeviceConfig(NodeContainer<DeviceConfig.Builder> container, String changedField) {
        if ("device_class".equals(changedField)) {
            try {
                // clear the field in the builder and remove all child tree items representing these
                clearField(container, changedField);

                // create the new values for the field and add them to the builder  
                List<UnitConfig> builderList = new ArrayList<>();
                for (UnitTemplateConfig unitTemplate : remotePool.getDeviceRemote().getDeviceClassById(container.getBuilder().getDeviceClassId()).getUnitTemplateConfigList()) {
                    UnitConfig.Builder unitConfig = UnitConfig.newBuilder().setType(unitTemplate.getType()).setBoundToDevice(true);
                    unitConfig.setPlacementConfig(container.getBuilder().getPlacementConfig());
                    for (ServiceTemplate serviceTemplate : unitTemplate.getServiceTemplateList()) {
                        unitConfig.addServiceConfig(ServiceConfigType.ServiceConfig.newBuilder().setType(serviceTemplate.getServiceType()));
                    }
                    container.getBuilder().addUnitConfig(unitConfig);
                }

                // create and add a new child node container representing these children
                Descriptors.FieldDescriptor field = FieldDescriptorUtil.getField(DeviceConfig.UNIT_CONFIG_FIELD_NUMBER, container.getBuilder());
                container.add(new GenericListContainer(field.getName(), field, container.getBuilder(), builderList));
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory(logger, ex);
            }
        }
    }

    private static void clearField(NodeContainer<? extends GeneratedMessage.Builder> container, String fieldName) {
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

    public static boolean isModifiableList(GeneratedMessage.Builder builder, String fieldName) {
        if (builder instanceof DeviceClassType.DeviceClass.Builder && "unit_template_config".equals(fieldName)) {
            return false;
        } else if (builder instanceof UnitTemplateConfig.Builder && "service_template".equals(fieldName)) {
            return false;
        } else if (builder instanceof DeviceConfig.Builder && "unit_config".equals(fieldName)) {
            return false;
        } else if (builder instanceof UnitConfig.Builder && "service_config".equals(fieldName)) {
            return false;
        } else if (builder instanceof DeviceRegistryType.DeviceRegistry.Builder && "unit_template".equals(fieldName)) {
            return false;
        } else if (builder instanceof LocationConfigType.LocationConfig.Builder && "unit_id".equals(fieldName)) {
            return false;
        }
        return true;
    }

    public static boolean isModifiableField(GeneratedMessage.Builder builder, String fieldName) {
        if (builder instanceof UnitTemplateType.UnitTemplate.Builder) {
            return !("type".equals(fieldName) || "id".equals(fieldName));
        } else if (builder instanceof DeviceClassType.DeviceClass.Builder) {
            if ("product_number".equals(fieldName)) {
                return ((DeviceClassType.DeviceClass.Builder) builder).getProductNumber().isEmpty();
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
        } else if (builder instanceof ScopeType.Scope.Builder) {
            return !("scope".equals(fieldName));
        } else if (builder instanceof UnitConfig.Builder) {
            return !("id".equals(fieldName) || "device_id".equals(fieldName) || "type".equals(fieldName));
        } else if (builder instanceof ServiceConfigType.ServiceConfig.Builder) {
            return !("type".equals(fieldName) || "unit_id".equals(fieldName));
        } else if (builder instanceof LocationConfigType.LocationConfig.Builder) {
            return !("id".equals(fieldName) || "root".equals(fieldName));
        } else if (builder instanceof SceneConfigType.SceneConfig.Builder) {
            return !("id".equals(fieldName));
        } else if (builder instanceof AgentConfigType.AgentConfig.Builder) {
            return !("id".equals(fieldName));
        } else if (builder instanceof AppConfigType.AppConfig.Builder) {
            return !("id".equals(fieldName));
        }
        return true;
    }
}
