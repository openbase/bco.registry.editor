/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.binding.OpenHABBindingConfigType.OpenHABBindingConfig;

/**
 *
 * @author thuxohl
 */
public class OpenHABBindingConfigContainer extends NodeContainer<OpenHABBindingConfig.Builder>{

    public OpenHABBindingConfigContainer(OpenHABBindingConfig.Builder openhabBindingConfig) {
        super("openhab_binding_config", openhabBindingConfig);
        super.add(openhabBindingConfig.getOpenhabConfig(), "openhab_config");
    }
    
}
