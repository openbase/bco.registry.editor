/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.exception.printer.ExceptionPrinter;
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
        super.add(deviceConfig.getSerialNumber(), "serial_number", deviceConfig.getSerialNumber().isEmpty());
        super.add(new PlacementConfigContainer(deviceConfig.getPlacementConfigBuilder()));
        super.add(ScopeGenerator.generateStringRep(deviceConfig.getScope()), "scope", false);
        super.add(new InventoryStateContainer(deviceConfig.getInventoryStateBuilder()));
        super.add(deviceConfig.getDeviceClassId(), "device_class_id");
        super.add(new UnitConfigListContainer(deviceConfig));

        // TODO Tamino: implement global exception handling if gui elements are not able to init.
        try {
            super.add(new MetaConfigContainer(deviceConfig.getMetaConfigBuilder()));
        } catch (de.citec.jul.exception.InstantiationException ex) {
            ExceptionPrinter.printHistory(logger, ex);
        }

        super.add(deviceConfig.getDescription(), "description");
    }
}
