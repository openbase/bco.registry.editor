/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.exception.ExceptionPrinter;
import rst.homeautomation.binding.BindingConfigType.BindingConfig;

/**
 *
 * @author thuxohl
 */
public class BindingConfigContainer extends NodeContainer<BindingConfig.Builder> {

    public BindingConfigContainer(BindingConfig.Builder bindingConfig) {
        super("binding_config", bindingConfig);
        super.add(bindingConfig.getType(), "type");

        try {
            super.add(new MetaConfigContainer(bindingConfig.getMetaConfigBuilder()));
        } catch (de.citec.jul.exception.InstantiationException ex) {
            ExceptionPrinter.printHistory(logger, ex);
        }
    }

}
