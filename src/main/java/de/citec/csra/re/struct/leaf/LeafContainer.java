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
import rst.homeautomation.unit.UnitTypeHolderType;
import rst.homeautomation.unit.UnitTypeHolderType.UnitTypeHolder.UnitType;

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
            } else if (value instanceof UnitType) {
                parent.getBuilder().setRepeatedField(field, index, UnitTypeHolderType.UnitTypeHolder.newBuilder().setUnitType(((UnitType) value)).build());
            }
        } else if (index == -1) {
            parent.getBuilder().setField(field, value);
        } else {
            parent.getBuilder().setRepeatedField(field, index, value);
        }

        parent.setSendableChanged();
    }

//    @Override
    public Node getThis() {
        return this;
    }
}
