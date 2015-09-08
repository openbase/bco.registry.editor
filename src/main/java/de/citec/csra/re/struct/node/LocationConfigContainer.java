/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.extension.rsb.scope.ScopeGenerator;
import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author thuxohl
 */
public class LocationConfigContainer extends SendableNode<LocationConfig.Builder> {

    public LocationConfigContainer(LocationConfig.Builder location) {
        super(location.getLabel(), location);
        super.add(location.getId(), "id", false);
        super.add(location.getLabel(), "label");
        super.add(location.getParentId(), "parent_id");
        super.add(ScopeGenerator.generateStringRep(location.getScope()), "scope", false);
        super.add(new UnitIdListContainer(location));
        super.add(new PositionContainer(location.getPositionBuilder()));
        super.add(new ChildLocationListContainer(location));
        
        // TODO Tamino: implement global exception handling if gui elements are not able to init.
        try {
            super.add(new MetaConfigContainer(location.getMetaConfigBuilder()));
        } catch (de.citec.jul.exception.InstantiationException ex) {
            ExceptionPrinter.printHistory(logger, ex);
        }
        
        super.add(location.getDescription(), "description");
    }
}
