/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import de.citec.csra.re.util.FieldDescriptorUtil;
import de.citec.csra.re.util.RemotePool;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.ExceptionPrinter;
import de.citec.jul.exception.InstantiationException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.service.ServiceConfigType;
import rst.homeautomation.service.ServiceTemplateType.ServiceTemplate;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.homeautomation.unit.UnitTemplateConfigType.UnitTemplateConfig;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class Adjuster {

    private final RemotePool remotePool;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // TODO: wrap it up in a good structure and find a good namespace
    public Adjuster() throws InstantiationException {
        this.remotePool = RemotePool.getInstance();
    }

    public void adjust(NodeContainer<UnitTemplateConfig.Builder> container, String changedField) {
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

    public void adjust1(NodeContainer<DeviceConfig.Builder> container, String changedField) {
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

    private void clearField(NodeContainer<? extends GeneratedMessage.Builder> container, String fieldName) {
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
}
