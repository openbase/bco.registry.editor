/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.leaf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ProtocolMessageEnum;
import de.citec.csra.re.struct.node.GenericListContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.csra.re.struct.node.NodeContainer;
import de.citec.csra.re.struct.node.RotationContainer;
import de.citec.csra.re.struct.node.ServiceTemplateContainer;
import de.citec.csra.re.struct.node.UnitTemplateConfigContainer;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jul.exception.printer.ExceptionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.service.ServiceTemplateType;
import rst.homeautomation.unit.UnitTemplateConfigType;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate;

/**
 *
 * @author thuxohl
 * @param <T>
 */
public class LeafContainer<T> implements Leaf<T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private T value;
    private final boolean editable;
    private final String descriptor;
    private final NodeContainer parent;
    private final int index;

    public LeafContainer(T value, String descriptor, NodeContainer parent) {
        this.value = value;
        this.descriptor = descriptor;
        this.parent = parent;
        this.index = -1;
        editable = true;
    }

    public LeafContainer(T value, String descriptor, NodeContainer parent, int index) {
        this.value = value;
        this.descriptor = descriptor;
        this.parent = parent;
        this.index = index;
        editable = true;
    }

    public LeafContainer(T value, String descriptor, NodeContainer parent, boolean editable) {
        this.value = value;
        this.descriptor = descriptor;
        this.parent = parent;
        this.index = -1;
        this.editable = editable;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    public NodeContainer getParent() {
        return parent;
    }

    public int getIndex() {
        return index;
    }

    public boolean getEditable() {
        return editable;
    }

    @Override
    public void setValue(T value) {
        this.value = value;

//        System.out.println("Value = "+ value);
//        System.out.println("Container [" + parent.getDescriptor() + "], expected field name [" + descriptor + "]");
        Descriptors.FieldDescriptor field = parent.getBuilder().getDescriptorForType().findFieldByName(descriptor);
//        System.out.println("field:fullname" + field.getFullName());
//        System.out.println("field:name" + field.getName());
//        System.out.println("field:type" + field.getType());
//        System.out.println("field:Jvatype" + field.getJavaType());
//        System.out.println("getValue():" + getValue());
//        System.out.println("getValue().Class:" + getValue().getClass());

        if (value instanceof ProtocolMessageEnum) {
            if (index == -1) {
                parent.getBuilder().setField(field, ((ProtocolMessageEnum) getValue()).getValueDescriptor());
            } else {
                parent.getBuilder().setRepeatedField(field, index, ((ProtocolMessageEnum) getValue()).getValueDescriptor());
            }
        } else if (index == -1) {
            if (descriptor.equals("roll")) {
                ((RotationContainer) parent).setRoll((Double) value);
            } else if (descriptor.equals("pitch")) {
                ((RotationContainer) parent).setPitch((Double) value);
            } else if (descriptor.equals("yaw")) {
                ((RotationContainer) parent).setYaw((Double) value);
            } else {
                parent.getBuilder().setField(field, value);
            }
        } else {
            parent.getBuilder().setRepeatedField(field, index, value);
        }

        if (value instanceof UnitTemplate.UnitType) {
            try {
                UnitTemplate.UnitType type = (UnitTemplate.UnitType) value;
                UnitTemplateConfigContainer container = (UnitTemplateConfigContainer) this.getParent();

                for (int i = 0; i < container.getChildren().size(); i++) {
                    Node item = container.getChildren().get(i).getValue();
                    if (item instanceof NodeContainer && ((NodeContainer) item).getDescriptor().equals("service_templates")) {
                        container.getChildren().remove(i);
                        i--;
                    }
                }

                DeviceRegistryRemote remote = new DeviceRegistryRemote();
                remote.init();
                remote.activate();
                container.getBuilder().clearServiceTemplate();
                for (ServiceTemplateType.ServiceTemplate.ServiceType serviceType : remote.getUnitTemplateByType(type).getServiceTypeList()) {
                    ServiceTemplateType.ServiceTemplate.Builder serviceTemplateBuilder = ServiceTemplateType.ServiceTemplate.newBuilder().setServiceType(serviceType);
                    container.getBuilder().addServiceTemplate(serviceTemplateBuilder);
                }
                try {
                    container.add(new GenericListContainer(UnitTemplateConfigType.UnitTemplateConfig.SERVICE_TEMPLATE_FIELD_NUMBER, container.getBuilder(), ServiceTemplateContainer.class));
                } catch (de.citec.jul.exception.InstantiationException ex) {
                    ExceptionPrinter.printHistory(logger, ex);
                }
            } catch (Exception ex) {
                ExceptionPrinter.printHistory(logger, ex);
            }
        }

        parent.setSendableChanged();
    }

    @Override
    public Node getContext() {
        return this;
    }
}
