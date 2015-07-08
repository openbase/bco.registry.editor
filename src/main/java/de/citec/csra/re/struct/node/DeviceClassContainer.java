/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.exception.ExceptionPrinter;
import rst.homeautomation.device.DeviceClassType.DeviceClass;

/**
 *
 * @author thuxohl
 */
public class DeviceClassContainer extends SendableNode<DeviceClass.Builder> {
    
    public DeviceClassContainer(final DeviceClass.Builder deviceClass) {
        super(deviceClass.getId(), deviceClass);
        super.add(deviceClass.getId(), "id", false);
        super.add(deviceClass.getLabel(), "label");
        super.add(deviceClass.getProductNumber(), "product_number");
        super.add(new BindingConfigContainer(deviceClass.getBindingConfigBuilder()));
        super.add(new UnitTemplateListContainer(deviceClass));
        super.add(deviceClass.getCompany(), "company");
        
        // TODO Tamino: implement global exception handling if gui elements are not able to init.
        try {
            super.add(new MetaConfigContainer(deviceClass.getMetaConfigBuilder()));
        } catch (de.citec.jul.exception.InstantiationException ex) {
            ExceptionPrinter.printHistory(logger, ex);
        }
        
        super.add(deviceClass.getDescription(), "description");
    }
}
