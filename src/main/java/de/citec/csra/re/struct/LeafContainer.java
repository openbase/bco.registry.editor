/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct;

import com.google.protobuf.ProtocolMessageEnum;
import de.citec.jul.exception.CouldNotPerformException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thuxohl
 */
public class LeafContainer implements Leaf {
    
    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    
    private final boolean editable;
    private final String fieldName;
    private final NodeContainer parent;
    private final int index;
    private Object value;
    
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
        logger.info("Init leaf [" + fieldName + "," + value + "," + editable + "," + index + "]");
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
        
        try {
            if (value instanceof ProtocolMessageEnum) {
                parent.updateBuilder(getDescriptor(), (ProtocolMessageEnum) value, index);
            } else {
                parent.updateBuilder(getDescriptor(), value, index);
            }
        } catch (CouldNotPerformException ex) {
            Logger.getLogger(LeafContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public Node getContext() {
        return this;
    }
}
