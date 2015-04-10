/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.service.BindingServiceConfigType.BindingServiceConfig;

/**
 *
 * @author thuxohl
 */
public class BindingServiceConfigContainer extends NodeContainer<BindingServiceConfig.Builder>{

    public BindingServiceConfigContainer(BindingServiceConfig.Builder bindingServiceConfig) {
        super("binding_service_config", bindingServiceConfig);
        super.add(bindingServiceConfig.getType(), "type");
        super.add(new OpenHABBindingServiceConfigContainer(bindingServiceConfig.getOpenhabBindingServiceConfigBuilder()));
        super.add(new MieleAtHomeBindingServiceConfigContainer(bindingServiceConfig.getMieleAtHomeBindingServiceConfigBuilder()));
    }
    
}
