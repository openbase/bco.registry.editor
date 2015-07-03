/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.state.ActivationStateType.ActivationState;

/**
 *
 * @author thuxohl
 */
public class ActivationStateContainer extends NodeContainer<ActivationState.Builder> {

    public ActivationStateContainer(String descriptor, ActivationState.Builder builder) {
        super(descriptor, builder);
        System.out.println("Actication container value ["+builder.getValue()+"]");
        super.add(builder.getValue(), "value");
    }
}
