/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.service.SinactBindingServiceConfigType.SinactBindingServiceConfig;

/**
 *
 * @author thuxohl
 */
class SinactBindingServiceConfigContainer extends NodeContainer<SinactBindingServiceConfig.Builder> {

    public SinactBindingServiceConfigContainer(SinactBindingServiceConfig.Builder sinactBindingServiceConfig) {
        super("Sinact Service Configuration", sinactBindingServiceConfig);
        super.add(sinactBindingServiceConfig.getHardwareConfig(), "hardware_config");
    }

}
