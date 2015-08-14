/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.device.DeviceRegistryType;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class UnitTemplateListContainer extends NodeContainer<DeviceRegistryType.DeviceRegistry.Builder> {

    public UnitTemplateListContainer(final DeviceRegistryType.DeviceRegistry.Builder deviceRegistry) {
        super("Unit Templates", deviceRegistry);
        deviceRegistry.getUnitTemplateBuilderList().stream().forEach((unitTemplateBuilder) -> {
            super.add(new UnitTemplateContainer(unitTemplateBuilder));
        });
    }
}
