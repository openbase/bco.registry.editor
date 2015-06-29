/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import java.util.ArrayList;
import java.util.List;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType;
import rst.homeautomation.device.DeviceRegistryType.DeviceRegistry;

/**
 *
 * @author thuxohl
 */
public class DeviceConfigGroupContainer extends NodeContainer<DeviceRegistry.Builder> {

    private final DeviceClass deviceClass;
    
    public DeviceConfigGroupContainer(final DeviceRegistry.Builder deviceRegistry, DeviceClass deviceClass) {
        super(deviceClass.getId(), deviceRegistry);
        this.deviceClass = deviceClass;
        List<DeviceConfigType.DeviceConfig.Builder> deviceConfigs = new ArrayList<>();
        deviceRegistry.getDeviceConfigBuilderList().stream().forEach((deviceConfigBuilder) -> {
            if (deviceConfigBuilder.getDeviceClass().getId().equals(deviceClass.getId())) {
                deviceConfigs.add(deviceConfigBuilder);
            }
        });

        List<String> locationIds = new ArrayList<>();
        for (DeviceConfigType.DeviceConfig.Builder deviceConfig : deviceConfigs) {
            if(!locationIds.contains(deviceConfig.getPlacementConfig().getLocationId())) {
                locationIds.add(deviceConfig.getPlacementConfig().getLocationId());
            }
        }

        for (String locationId : locationIds) {
            super.add(new LocationGroupContainer(deviceRegistry, deviceConfigs, locationId));
        }
    }

    public DeviceClass getDeviceClass() {
        return deviceClass;
    }
}
