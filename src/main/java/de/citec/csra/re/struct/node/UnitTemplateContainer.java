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
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class UnitTemplateContainer extends SendableNode<UnitTemplate.Builder> {
    
    public UnitTemplateContainer(UnitTemplate.Builder value) {
        super("unit_template", value);
        super.add(value.getId(), "id", false);
        super.add(value.getType(), "type", false);
        super.add(new ServiceTypeListContainer(value));
        try {
            super.add(new GenericListContainer(UnitTemplate.META_CONFIG_FIELD_NUMBER, builder, ServiceTemplateContainer.class));
        } catch (de.citec.jul.exception.InstantiationException ex) {
            ExceptionPrinter.printHistory(logger, ex);
        }
    }
    
}
