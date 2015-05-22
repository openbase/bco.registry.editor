/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import java.util.List;
import rst.homeautomation.device.DeviceConfigType;
import rst.homeautomation.device.DeviceRegistryType.DeviceRegistry;

/**
 *
 * @author thuxohl
 */
public class LocationGroupContainer extends NodeContainer<DeviceRegistry.Builder> {

    public LocationGroupContainer(final DeviceRegistry.Builder deviceRegistry, List<DeviceConfigType.DeviceConfig.Builder> devices, String locationId) {
        super(locationId, deviceRegistry);
        deviceRegistry.getDeviceConfigBuilderList().stream().forEach((deviceConfigBuilder) -> {
            if (devices.contains(deviceConfigBuilder) && deviceConfigBuilder.getPlacementConfig().getLocationId().equals(locationId)) {
                super.add(new DeviceConfigContainer(deviceConfigBuilder));
            }
        });
    }
}
