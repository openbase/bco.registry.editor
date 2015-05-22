/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceRegistryType.DeviceRegistry;

/**
 *
 * @author thuxohl
 */
public class DeviceConfigGroupContainer extends NodeContainer<DeviceRegistry.Builder> {

    public DeviceConfigGroupContainer(final DeviceRegistry.Builder deviceRegistry, DeviceClass deviceClass) {
        super(deviceClass.getId() + "'s", deviceRegistry);
        deviceRegistry.getDeviceConfigBuilderList().stream().forEach((deviceConfigBuilder) -> {
            if (deviceConfigBuilder.getDeviceClass().equals(deviceClass)) {
                super.add(new DeviceConfigContainer(deviceConfigBuilder));
            }
        });
    }
}
