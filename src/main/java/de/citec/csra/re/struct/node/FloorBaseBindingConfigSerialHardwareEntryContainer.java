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
public class FloorBaseBindingConfigSerialHardwareEntryContainer extends NodeContainer<FloorBaseBindingConfig.SerialHardwareEntry.Builder> {

    public FloorBaseBindingConfigSerialHardwareEntryContainer(FloorBaseBindingConfig.SerialHardwareEntry.Builder builder) {
        super("serial_hardware_mapping", builder);
        super.add(builder.getSerialNumber(), "serial_number");
        super.add(builder.getHardwareId(), "hardware_id");
    }
}
