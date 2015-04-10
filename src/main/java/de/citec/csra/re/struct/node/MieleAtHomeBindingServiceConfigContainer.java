/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.service.MieleAtHomeBindingServiceConfigType.MieleAtHomeBindingServiceConfig;

/**
 *
 * @author thuxohl
 */
class MieleAtHomeBindingServiceConfigContainer extends NodeContainer<MieleAtHomeBindingServiceConfig.Builder> {

    public MieleAtHomeBindingServiceConfigContainer(MieleAtHomeBindingServiceConfig.Builder mieleAtHomeBindingServiceConfig) {
        super("Miele@Home Binding Service Configuration", mieleAtHomeBindingServiceConfig);
        super.add(mieleAtHomeBindingServiceConfig.getItemId(), "item_id");
        super.add(mieleAtHomeBindingServiceConfig.getItemHardwareConfig(), "hardware_config");
    }

}
