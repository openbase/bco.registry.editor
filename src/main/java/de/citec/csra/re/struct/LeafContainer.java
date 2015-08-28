/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ProtocolMessageEnum;
import de.citec.csra.re.util.FieldUtil;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thuxohl
 */
public class LeafContainer implements Leaf {

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    private Object value;
    private final boolean editable;
    private final String fieldName;
    private final NodeContainer parent;
    private final int index;

    public LeafContainer(Object value, String fieldName, NodeContainer parent) {
        this(value, fieldName, parent, true, -1);
    }

    public LeafContainer(Object value, String fieldName, NodeContainer parent, int index) {
        this(value, fieldName, parent, true, index);
    }

    public LeafContainer(Object value, String fieldName, NodeContainer parent, boolean editable) {
        this(value, fieldName, parent, editable, -1);
    }

    public LeafContainer(Object value, String fieldName, NodeContainer parent, boolean editable, int index) {
        this.value = value;
        this.fieldName = fieldName;
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
        return fieldName;
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

//        if (value instanceof ProtocolMessageEnum) {
//            if (index == -1) {
//                parent.getBuilder().setField(fieldName, ((ProtocolMessageEnum) this.value).getValueDescriptor());
//            } else {
//                parent.getBuilder().setRepeatedField(fieldName, index, ((ProtocolMessageEnum) this.value).getValueDescriptor());
//            }
//        } else if (index == -1) {
//            parent.getBuilder().setField(fieldName, this.value);
//        } else {
//            parent.getBuilder().setRepeatedField(fieldName, index, this.value);
//        }

        parent.setSendableChanged();
    }

    @Override
    public Node getContext() {
        return this;
    }

    @Override
    public Descriptors.FieldDescriptor getFieldDescriptor() {
        return FieldUtil.getField(fieldName, parent.getBuilder());
    }
}
