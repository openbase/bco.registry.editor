/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.exception.ExceptionPrinter;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate;

/**
 *
 * @author thuxohl
 */
public class UnitTemplateContainer extends VariableNode<UnitTemplate.Builder>{

    public UnitTemplateContainer(UnitTemplate.Builder unitTemplate) {
        super("unit_template", unitTemplate);
        super.add(unitTemplate.getType(), "type");
        super.add(new ServiceTypeListContainer(unitTemplate));

        // TODO Tamino: implement global exception handling if gui elements are not able to init.
        try {
            super.add(new GenericListContainer(UnitTemplate.SERVICE_TEMPLATE_FIELD_NUMBER, builder, ServiceTemplateContainer.class));
        } catch (de.citec.jul.exception.InstantiationException ex) {
            ExceptionPrinter.printHistory(logger, ex);
        }
    }
    
}
