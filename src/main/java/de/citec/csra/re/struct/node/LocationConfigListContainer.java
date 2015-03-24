/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.spatial.LocationRegistryType;

/**
 *
 * @author thuxohl
 */
public class LocationConfigListContainer extends NodeContainer<LocationRegistryType.LocationRegistry.Builder> {

    public LocationConfigListContainer(final LocationRegistryType.LocationRegistry.Builder locationRegistry) {
        super("Location Configurations", locationRegistry);
        locationRegistry.getLocationConfigsBuilderList().stream().forEach((locationConfigBuilder) -> {
            super.add(new LocationConfigContainer(locationConfigBuilder));
        });
    }
}
