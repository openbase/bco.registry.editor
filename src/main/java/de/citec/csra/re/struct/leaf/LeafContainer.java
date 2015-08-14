/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.leaf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ProtocolMessageEnum;
import de.citec.csra.re.struct.node.Node;
import de.citec.csra.re.struct.node.NodeContainer;
import de.citec.csra.re.struct.node.RotationContainer;

/**
 *
 * @author thuxohl
 * @param <T>
 */
public class LeafContainer<T> implements Leaf<T> {

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

//        if (value instanceof DeviceClass) {
//            DeviceClass deviceClass = (DeviceClass) value;
//            DeviceConfigContainer container = (DeviceConfigContainer) this.getParent();
//
//            for (int i = 0; i < container.getChildren().size(); i++) {
//                Node item = container.getChildren().get(i).getValue();
//                if (item instanceof NodeContainer && ((NodeContainer) item).getDescriptor().equals("unit_configs")) {
//                    container.getChildren().remove(i);
//                    i--;
//                }
//            }
//
//            container.getBuilder().clearUnitConfig();
//            for (UnitTemplate unitTemplate : deviceClass.getUnitTemplateList()) {
//                UnitConfigType.UnitConfig.Builder unitConfigBuilder = UnitConfigType.UnitConfig.newBuilder().setTemplate(unitTemplate);
//                for (ServiceTypeHolderType.ServiceTypeHolder.ServiceType serviceType : unitTemplate.getServiceTypeList()) {
//                    unitConfigBuilder.addServiceConfig(ServiceConfigType.ServiceConfig.newBuilder().setType(serviceType));
//                }
//                container.getBuilder().addUnitConfig(RSTDefaultInstances.setDefaultPlacement(unitConfigBuilder).build());
//            }
//            container.add(new UnitConfigListContainer(container.getBuilder()));
//        }

        parent.setSendableChanged();
    }

    @Override
    public Node getContext() {
        return this;
    }
}
