/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.binding.BindingConfigType.BindingConfig;

/**
 *
 * @author thuxohl
 */
public class BindingConfigContainer extends NodeContainer<BindingConfig.Builder> {

    public BindingConfigContainer(BindingConfig.Builder bindingConfig) {
        super("binding_config", bindingConfig);
        super.add(bindingConfig.getType(), "type");
        super.add(new OpenHABBindingConfigContainer(bindingConfig.getOpenhabBindingConfigBuilder()));
        super.add(new MieleAtHomeBindingConfigContainer(bindingConfig.getMieleAtHomeBindingConfigBuilder()));
        super.add(new FloorBaseBindingConfigContainer(bindingConfig.getFloorBaseBindingConfigBuilder()));
    }

}
