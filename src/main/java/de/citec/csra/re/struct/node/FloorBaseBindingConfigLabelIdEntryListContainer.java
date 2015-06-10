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
public class FloorBaseBindingConfigLabelIdEntryListContainer extends NodeContainer<FloorBaseBindingConfig.Builder> {

    public FloorBaseBindingConfigLabelIdEntryListContainer(FloorBaseBindingConfig.Builder builder) {
        super("label_id_entry", builder);
        
        builder.getLabelIdEntryBuilderList().stream().forEach((labelIdEntryBuilder) -> {
            super.add(new FloorBaseBindingConfigLabelIdEntryContainer(labelIdEntryBuilder));
        });
    }

}
