/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.service.HandlesBindingServiceConfigType.HandlesBindingServiceConfig;

/**
 *
 * @author thuxohl
 */
class HandlesBindingServiceConfigContainer extends NodeContainer<HandlesBindingServiceConfig.Builder> {

    public HandlesBindingServiceConfigContainer(HandlesBindingServiceConfig.Builder handlesBindingServiceConfig) {
        super("Handles Binding Service Configuration", handlesBindingServiceConfig);
        super.add(handlesBindingServiceConfig.getHardwareConfig(), "hardware_config");
    }

}
