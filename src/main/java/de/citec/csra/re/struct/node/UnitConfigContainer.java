/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.extension.rsb.scope.ScopeGenerator;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;

/**
 *
 * @author thuxohl
 */
public class UnitConfigContainer extends NodeContainer<UnitConfig.Builder> {

    public UnitConfigContainer(UnitConfig.Builder unitConfig) {
        super(unitConfig.getLabel(), unitConfig);
        super.add(unitConfig.getId(), "id", false);
        super.add(unitConfig.getLabel(), "label");
        super.add(unitConfig.getType(), "type", false);
        super.add(unitConfig.getBoundToDevice(), "bound_to_device");
        super.add(new PlacementConfigContainer(unitConfig.getPlacementConfigBuilder()));
        super.add(new ServiceConfigListContainer(unitConfig));
        super.add(ScopeGenerator.generateStringRep(unitConfig.getScope()), "scope", false);
        // TODO Tamino: implement global exception handling if gui elements are not able to init.
        try {
            super.add(new MetaConfigContainer(builder.getMetaConfigBuilder()));
        } catch (de.citec.jul.exception.InstantiationException ex) {
            ExceptionPrinter.printHistory(logger, ex);
        }
        super.add(unitConfig.getDescription(), "description");
    }
}
