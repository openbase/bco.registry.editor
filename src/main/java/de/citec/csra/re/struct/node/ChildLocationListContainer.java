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
public class ChildLocationListContainer extends NodeContainer<LocationConfig.Builder> {
    
    public ChildLocationListContainer(final LocationConfig.Builder parentLocationConfig) {
        super("child_ids", parentLocationConfig);
        parentLocationConfig.getChildIdList().stream().forEach((childId) -> {
            super.add(childId, "child_id", parentLocationConfig.getChildIdList().indexOf(childId));
        });
    }
}
