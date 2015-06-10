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
public class FloorBaseBindingConfigLabelIdEntryContainer extends NodeContainer<FloorBaseBindingConfig.LabelIdEntry.Builder> {

    public FloorBaseBindingConfigLabelIdEntryContainer(FloorBaseBindingConfig.LabelIdEntry.Builder builder) {
        super("label_id_entry", builder);
        super.add(builder.getLabel(), "label");
        super.add(builder.getHardwareId(), "hardware_id");
    }
}
