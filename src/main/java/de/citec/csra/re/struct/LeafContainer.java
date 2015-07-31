/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ProtocolMessageEnum;

/**
 *
 * @author thuxohl
 */
public class LeafContainer implements Leaf {

    private Object value;
    private final boolean editable;
    private final Descriptors.FieldDescriptor fieldDescriptor;
    private final NodeContainer parent;
    private final int index;

    public LeafContainer(Object value, Descriptors.FieldDescriptor fieldDescriptor, NodeContainer parent) {
        this(value, fieldDescriptor, parent, true, -1);
    }

    public LeafContainer(Object value, Descriptors.FieldDescriptor fieldDescriptor, NodeContainer parent, int index) {
        this(value, fieldDescriptor, parent, true, index);
    }

    public LeafContainer(Object value, Descriptors.FieldDescriptor fieldDescriptor, NodeContainer parent, boolean editable) {
        this(value, fieldDescriptor, parent, editable, -1);
    }

    public LeafContainer(Object value, Descriptors.FieldDescriptor fieldDescriptor, NodeContainer parent, boolean editable, int index) {
        this.value = value;
        this.fieldDescriptor = fieldDescriptor;
        this.parent = parent;
        this.index = index;
        this.editable = editable;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getDescriptor() {
        return fieldDescriptor.getName();
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
    public void setValue(Object value) {
        this.value = value;
        if (value instanceof ProtocolMessageEnum) {
            if (index == -1) {
                parent.getBuilder().setField(fieldDescriptor, ((ProtocolMessageEnum) getValue()).getValueDescriptor());
            } else {
                parent.getBuilder().setRepeatedField(fieldDescriptor, index, ((ProtocolMessageEnum) getValue()).getValueDescriptor());
            }
        } else if (index == -1) {
            if (fieldDescriptor.equals("roll")) {
//                ((RotationContainer) parent).setRoll((Double) value);
//            } else if (fieldDescriptor.equals("pitch")) {
//                ((RotationContainer) parent).setPitch((Double) value);
//            } else if (fieldDescriptor.equals("yaw")) {
//                ((RotationContainer) parent).setYaw((Double) value);
            } else {
                parent.getBuilder().setField(fieldDescriptor, value);
            }
        } else {
            parent.getBuilder().setRepeatedField(fieldDescriptor, index, value);
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

    @Override
    public Descriptors.FieldDescriptor getFieldDescriptor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
