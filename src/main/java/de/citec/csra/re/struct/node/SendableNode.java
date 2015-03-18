/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import com.google.protobuf.GeneratedMessage;

/**
 *
 * @author thuxohl
 * @param <MB>
 */
public abstract class SendableNode<MB extends GeneratedMessage.Builder> extends VariableNode<MB> {

    private boolean newNode;

    public SendableNode(String descriptor, MB value) {
        super(descriptor, value);
        newNode = false;
    }

    public void setNewNode() {
        newNode = true;
    }
    
    public boolean isNewNode() {
        return newNode;
    }
}
