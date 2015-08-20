/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct;

import com.google.protobuf.Descriptors;
import com.google.protobuf.ProtocolMessageEnum;
import de.citec.csra.re.struct.transform.DefaultValueTransformer;
import de.citec.csra.re.struct.transform.ValueTransformer;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.ExceptionPrinter;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thuxohl
 */
public class LeafContainer implements Leaf {

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    private Object value;
    private final boolean editable;
    private final Descriptors.FieldDescriptor fieldDescriptor;
    private final NodeContainer parent;
    private final int index;
    private final ValueTransformer valueTransformer;

    public LeafContainer(Object value, Descriptors.FieldDescriptor fieldDescriptor, NodeContainer parent) {
        this(value, fieldDescriptor, parent, true, -1, new DefaultValueTransformer());
    }

    public LeafContainer(Object value, Descriptors.FieldDescriptor fieldDescriptor, NodeContainer parent, int index) {
        this(value, fieldDescriptor, parent, true, index, new DefaultValueTransformer());
    }

    public LeafContainer(Object value, Descriptors.FieldDescriptor fieldDescriptor, NodeContainer parent, boolean editable) {
        this(value, fieldDescriptor, parent, editable, -1, new DefaultValueTransformer());
    }

    public LeafContainer(Object value, Descriptors.FieldDescriptor fieldDescriptor, NodeContainer parent, ValueTransformer valueTransformer) {
        this(value, fieldDescriptor, parent, true, -1, valueTransformer);
    }

    public LeafContainer(Object value, Descriptors.FieldDescriptor fieldDescriptor, NodeContainer parent, boolean editable, int index, ValueTransformer valueTransformer) {
        this.value = value;
        this.fieldDescriptor = fieldDescriptor;
        this.parent = parent;
        this.index = index;
        this.editable = editable;
        this.valueTransformer = valueTransformer;
    }

    @Override
    public Object getValue() {
        try {
            return valueTransformer.transformToVisual(value);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
            return value;
        }
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
        try {
            this.value = valueTransformer.transformToInternal(value);
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
            this.value = value;
        }

        if (value instanceof ProtocolMessageEnum) {
            if (index == -1) {
                parent.getBuilder().setField(fieldDescriptor, ((ProtocolMessageEnum) this.value).getValueDescriptor());
            } else {
                parent.getBuilder().setRepeatedField(fieldDescriptor, index, ((ProtocolMessageEnum) this.value).getValueDescriptor());
            }
        } else if (index == -1) {
            parent.getBuilder().setField(fieldDescriptor, this.value);
        } else {
            parent.getBuilder().setRepeatedField(fieldDescriptor, index, this.value);
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

    public ValueTransformer getValueTransformer() {
        return valueTransformer;
    }
}
