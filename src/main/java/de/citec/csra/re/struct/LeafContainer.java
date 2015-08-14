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
            parent.getBuilder().setField(fieldDescriptor, value);
        } else {
            parent.getBuilder().setRepeatedField(fieldDescriptor, index, value);
        }

        parent.setSendableChanged();
    }

    @Override
    public Node getContext() {
        return this;
    }

    @Override
    public Descriptors.FieldDescriptor getFieldDescriptor() {
        return fieldDescriptor;
    }
}
