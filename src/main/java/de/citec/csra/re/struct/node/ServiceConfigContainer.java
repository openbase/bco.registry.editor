/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.service.ServiceConfigType.ServiceConfig;

/**
 *
 * @author thuxohl
 */
public class ServiceConfigContainer extends NodeContainer<ServiceConfig.Builder> {

    public ServiceConfigContainer(ServiceConfig.Builder serviceConfig) {
        super("service_config", serviceConfig);
        super.add(serviceConfig.getType(), "type");
        super.add(serviceConfig.getUnitId(), "unit_id");
        super.add(new BindingServiceConfigContainer(serviceConfig.getBindingServiceConfigBuilder()));
    }
}
