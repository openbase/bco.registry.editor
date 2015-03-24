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
public class VariableNode<MB extends GeneratedMessage.Builder> extends NodeContainer<MB> {

    public VariableNode(String descriptor, MB value) {
        super(descriptor, value);
    } 
}
