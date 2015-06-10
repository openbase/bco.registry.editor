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
public class FloorBaseBindingConfigSerialHardwareEntryListContainer extends NodeContainer<FloorBaseBindingConfig.Builder> {

    public FloorBaseBindingConfigSerialHardwareEntryListContainer(FloorBaseBindingConfig.Builder builder) {
        super("serial_hardware_entry", builder);
        
        builder.getSerialHardwareEntryBuilderList().stream().forEach((serialHardwareEntryBuilder) -> {
            super.add(new FloorBaseBindingConfigSerialHardwareEntryContainer(serialHardwareEntryBuilder));
        });
    }

}
