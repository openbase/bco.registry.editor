/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.rsb.scope.ScopeGenerator;
import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author thuxohl
 */
public class LocationConfigContainer extends SendableNode<LocationConfig.Builder> {

    public LocationConfigContainer(LocationConfig.Builder location) {
        super(location.getId(), location);
        super.add(location.getLabel(), "label");
        super.add(location.getParentId(), "parent_id");
        super.add(ScopeGenerator.generateStringRep(location.getScope()), "scope", false);
        super.add(new UnitIdListContainer(location));
        super.add(new ChildLocationListContainer(location));
    }
}
