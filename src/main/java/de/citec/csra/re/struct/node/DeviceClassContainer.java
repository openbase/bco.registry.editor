/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.device.DeviceClassType.DeviceClass;

/**
 *
 * @author thuxohl
 */
public class DeviceClassContainer extends SendableNode<DeviceClass.Builder> {
    
    public DeviceClassContainer(final DeviceClass.Builder deviceClass) {
        super(deviceClass.getId(), deviceClass);
        super.add(deviceClass.getLabel(), "label");
        super.add(deviceClass.getProductNumber(), "product_number");
        super.add(new BindingConfigContainer(deviceClass.getBindingConfigBuilder()));
        super.add(new UnitTemplateListContainer(deviceClass));
        super.add(deviceClass.getCompany(), "company");
        super.add(deviceClass.getDescription(), "description");
    }
}
