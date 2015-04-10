/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.binding.MieleAtHomeBindingConfigType.MieleAtHomeBindingConfig;

/**
 *
 * @author thuxohl
 */
public class MieleAtHomeBindingConfigContainer extends NodeContainer<MieleAtHomeBindingConfig.Builder>{

    public MieleAtHomeBindingConfigContainer(MieleAtHomeBindingConfig.Builder mieleAtHomeBindingConfig) {
        super("miele_at_home_binding_config", mieleAtHomeBindingConfig);
        super.add(mieleAtHomeBindingConfig.getMieleAtHomeConfig(), "miele_at_home_config");
    }
    
}
