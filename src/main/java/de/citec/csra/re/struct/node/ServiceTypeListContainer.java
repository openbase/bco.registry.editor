/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.homeautomation.unit.UnitTemplateType.UnitTemplate;

/**
 *
 * @author thuxohl
 */
public class ServiceTypeListContainer extends NodeContainer<UnitTemplate.Builder> {

    public ServiceTypeListContainer(UnitTemplate.Builder unitTemplate) {
        super("service_types", unitTemplate);
        unitTemplate.getServiceTypeList().stream().forEach((serviceType) -> {
            super.add(serviceType, "service_type", unitTemplate.getServiceTypeList().indexOf(serviceType));
        });
    }

}
