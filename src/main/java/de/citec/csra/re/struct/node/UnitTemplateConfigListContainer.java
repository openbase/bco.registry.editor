/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.device.DeviceClassType;

/**
 *
 * @author thuxohl
 */
public class UnitTemplateConfigListContainer extends NodeContainer<DeviceClassType.DeviceClass.Builder> {

    public UnitTemplateConfigListContainer(final DeviceClassType.DeviceClass.Builder deviceClass) {
        super("unit_template_config", deviceClass);
        deviceClass.getUnitTemplateConfigBuilderList().stream().forEach((unitTemplateConfigBuilder) -> {
            super.add(new UnitTemplateConfigContainer(unitTemplateConfigBuilder));
        });
    }
}
