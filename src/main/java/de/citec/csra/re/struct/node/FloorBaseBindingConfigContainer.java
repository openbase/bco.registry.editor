/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.binding.FloorBaseBindingConfigType.FloorBaseBindingConfig;

/**
 *
 * @author thuxohl
 */
public class FloorBaseBindingConfigContainer extends NodeContainer<FloorBaseBindingConfig.Builder>{

    public FloorBaseBindingConfigContainer(FloorBaseBindingConfig.Builder builder) {
        super("floor_base_binding_config", builder);
        super.add(builder.getUsbPort(), "usb_port");
        super.add(new FloorBaseBindingConfigSerialHardwareEntryListContainer(builder));
    }
    
}
