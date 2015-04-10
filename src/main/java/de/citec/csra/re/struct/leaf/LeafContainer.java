/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.leaf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ProtocolMessageEnum;
import de.citec.csra.re.cellfactory.DescriptionCell;
import de.citec.csra.re.struct.node.DeviceConfigContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.csra.re.struct.node.NodeContainer;
import de.citec.csra.re.struct.node.UnitConfigListContainer;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.service.ServiceConfigType;
import rst.homeautomation.service.ServiceTypeHolderType;
import rst.homeautomation.unit.UnitConfigType;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate;

/**
 *
 * @author thuxohl
 * @param <T>
 */
public class LeafContainer<T> implements Leaf<T> {

    private T value;
    private final String descriptor;
    private final NodeContainer parent;
    private final int index;

    public LeafContainer(T value, String descriptor, NodeContainer parent) {
        this.value = value;
        this.descriptor = descriptor;
        this.parent = parent;
        this.index = -1;
    }

    public LeafContainer(T value, String descriptor, NodeContainer parent, int index) {
        this.value = value;
        this.descriptor = descriptor;
        this.parent = parent;
        this.index = index;
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

    @Override
    public void setValue(T value) {
        this.value = value;

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
            parent.getBuilder().setField(field, value);
        } else {
            parent.getBuilder().setRepeatedField(field, index, value);
        }

        if (value instanceof DeviceClass) {
            DeviceClass deviceClass = (DeviceClass) value;
            DeviceConfigContainer container = (DeviceConfigContainer) this.getParent();
            container.getBuilder().clearUnitConfig();
            for (UnitTemplate unit : deviceClass.getUnitList()) {
                UnitConfigType.UnitConfig.Builder unitConfigBuilder = UnitConfigType.UnitConfig.newBuilder().setName(DescriptionCell.convertDescriptorT(unit.getType().toString()));
                for (ServiceTypeHolderType.ServiceTypeHolder.ServiceType serviceType : unit.getServiceTypeList()) {
                    unitConfigBuilder.addServiceConfig(ServiceConfigType.ServiceConfig.newBuilder().setType(serviceType));
                }
                container.getBuilder().addUnitConfig(unitConfigBuilder.build());
                container.add(new UnitConfigListContainer(container.getBuilder()));
            }
        }

        parent.setSendableChanged();
    }

    @Override
    public Node getThis() {
        return this;
    }
}
