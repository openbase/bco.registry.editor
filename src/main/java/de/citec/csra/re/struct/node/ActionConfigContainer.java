/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.extension.rsb.scope.ScopeGenerator;
import rst.homeautomation.control.action.ActionConfigType.ActionConfig;

/**
 *
 * @author thuxohl
 */
public class ActionConfigContainer extends NodeContainer<ActionConfig.Builder>{
    
    public ActionConfigContainer(String descriptor, ActionConfig.Builder value) {
        super(descriptor, value);
        super.add(value.getId(), "id", false);
        super.add(value.getLabel(), "label");
        super.add(value.getLocationId(), "location_id");
        super.add(ScopeGenerator.generateStringRep(value.getScope()), "scope", false);
        super.add(value.getDescription(), "description");
        super.add(new ActivationStateContainer("activation_state", value.getActivationStateBuilder()));
        
        try {
            super.add(new MetaConfigContainer(builder.getMetaConfigBuilder()));
        } catch (de.citec.jul.exception.InstantiationException ex) {
            ExceptionPrinter.printHistory(logger, ex);
        }
    }
}
