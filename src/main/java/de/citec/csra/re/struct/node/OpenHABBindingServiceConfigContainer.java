/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.service.OpenHABBindingServiceConfigType.OpenHABBindingServiceConfig;

/**
 *
 * @author thuxohl
 */
public class OpenHABBindingServiceConfigContainer extends NodeContainer<OpenHABBindingServiceConfig.Builder> {

    public OpenHABBindingServiceConfigContainer(OpenHABBindingServiceConfig.Builder openhabBindingServiceConfig) {
        super("openhab_service_configuration", openhabBindingServiceConfig);
        super.add(openhabBindingServiceConfig.getItemId(), "item_id");
        super.add(openhabBindingServiceConfig.getItemHardwareConfig(), "item_hardware_config");
    }
}
