/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.exception.InstantiationException;
import rst.homeautomation.unit.UnitTemplateType.UnitTemplate;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class UnitTemplateContainer extends SendableNode<UnitTemplate.Builder> {

    public UnitTemplateContainer(UnitTemplate.Builder value) {
        super(value.getId(), value);
        super.add(value.getId(), "id", false);
        super.add(value.getType(), "type", false);
        super.add(new ServiceTypeListContainer(value));
        try {
            super.add(new MetaConfigContainer(value.getMetaConfigBuilder()));
        } catch (InstantiationException ex) {
            ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
        }
    }

}
