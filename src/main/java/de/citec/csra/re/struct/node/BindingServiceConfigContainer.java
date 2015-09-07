/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.exception.printer.ExceptionPrinter;
import rst.homeautomation.service.BindingServiceConfigType.BindingServiceConfig;

/**
 *
 * @author thuxohl
 */
public class BindingServiceConfigContainer extends NodeContainer<BindingServiceConfig.Builder> {

    public BindingServiceConfigContainer(BindingServiceConfig.Builder bindingServiceConfig) {
        super("binding_service_config", bindingServiceConfig);
        super.add(bindingServiceConfig.getType(), "type");

        try {
            super.add(new MetaConfigContainer(bindingServiceConfig.getMetaConfigBuilder()));
        } catch (de.citec.jul.exception.InstantiationException ex) {
            ExceptionPrinter.printHistory(logger, ex);
        }
    }

}
