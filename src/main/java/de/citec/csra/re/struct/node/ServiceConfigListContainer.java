/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.unit.UnitConfigType;

/**
 *
 * @author thuxohl
 */
public class ServiceConfigListContainer extends NodeContainer<UnitConfigType.UnitConfig.Builder> {

    public ServiceConfigListContainer(final UnitConfigType.UnitConfig.Builder unitConfig) {
        super("service_configs", unitConfig);
        unitConfig.getServiceConfigsBuilderList().stream().forEach((serviceConfigBuilder) -> {
            super.add(new ServiceConfigContainer(serviceConfigBuilder));
        });
    }
}
