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
public class UnitIdListContainer extends NodeContainer<LocationConfig.Builder> {

    public UnitIdListContainer(final LocationConfig.Builder locationConfig) {
        super("units", locationConfig);
        locationConfig.getUnitIdList().stream().forEach((unitId) -> {
            super.add(unitId, "unit_id", locationConfig.getUnitIdList().indexOf(unitId));
        });
    }
}
