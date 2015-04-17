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
public class UnitTemplateListContainer extends NodeContainer<DeviceClassType.DeviceClass.Builder> {

    public UnitTemplateListContainer(final DeviceClassType.DeviceClass.Builder deviceClass) {
        super("units", deviceClass);
        deviceClass.getUnitBuilderList().stream().forEach((unitTemplateBuilder) -> {
            super.add(new UnitTemplateContainer(unitTemplateBuilder));
        });
    }
}
