/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.extension.rsb.scope.ScopeGenerator;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;

/**
 *
 * @author thuxohl
 */
public class DeviceConfigContainer extends SendableNode<DeviceConfig.Builder> {

    public DeviceConfigContainer(DeviceConfig.Builder deviceConfig) {
        super(deviceConfig.getLabel(), deviceConfig);
        super.add(deviceConfig.getId(), "id", false);
        super.add(deviceConfig.getLabel(), "label");
        super.add(deviceConfig.getSerialNumber(), "serial_number");
        super.add(new PlacementConfigContainer(deviceConfig.getPlacementConfigBuilder()));
        super.add(ScopeGenerator.generateStringRep(deviceConfig.getScope()), "scope", false);
        super.add(new InventoryStateContainer(deviceConfig.getInventoryStateBuilder()));
        super.add(deviceConfig.getDeviceClass(), "device_class");
        super.add(new UnitConfigListContainer(deviceConfig));
        super.add(deviceConfig.getDescription(), "description");
    }
}
