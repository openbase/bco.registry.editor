/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.exception.ExceptionPrinter;
import rst.homeautomation.unit.UnitTemplateConfigType.UnitTemplateConfig;

/**
 *
 * @author thuxohl
 */
public class UnitTemplateConfigContainer extends VariableNode<UnitTemplateConfig.Builder> {

    public UnitTemplateConfigContainer(UnitTemplateConfig.Builder unitTemplateConfig) {
        super("unit_template_config", unitTemplateConfig);
        super.add(unitTemplateConfig.getType(), "type", false);

        // TODO Tamino: implement global exception handling if gui elements are not able to init.
        try {
            super.add(new GenericListContainer(UnitTemplateConfig.SERVICE_TEMPLATE_FIELD_NUMBER, builder, ServiceTemplateContainer.class));
        } catch (de.citec.jul.exception.InstantiationException ex) {
            ExceptionPrinter.printHistory(logger, ex);
        }
    }

}
