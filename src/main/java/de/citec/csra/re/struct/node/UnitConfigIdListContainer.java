/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author thuxohl
 */
public class UnitConfigIdListContainer extends VariableNode<LocationConfig.Builder> {

    public UnitConfigIdListContainer(final LocationConfig.Builder locationConfig) {
        super("Units Configs", locationConfig);
        locationConfig.getUnitConfigIdsList().stream().forEach((unitId) -> {
            super.add(unitId, "unit_config_ids", locationConfig.getUnitConfigIdsList().indexOf(unitId));
        });
    }
}
