/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.extension.rsb.scope.ScopeGenerator;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;

/**
 *
 * @author thuxohl
 */
public class UnitConfigContainer extends NodeContainer<UnitConfig.Builder> {

    public UnitConfigContainer(UnitConfig.Builder unitConfig) {
        super(unitConfig.getId(), unitConfig);
        super.add(unitConfig.getId(), "Id", false);
        super.add(unitConfig.getLabel(), "label");
        super.add(unitConfig.getTemplate().getType(), "type");
        super.add(new PlacementConfigContainer(unitConfig.getPlacementConfigBuilder()));
        super.add(new ServiceConfigListContainer(unitConfig));
        super.add(ScopeGenerator.generateStringRep(unitConfig.getScope()), "scope", false);
        super.add(unitConfig.getDescription(), "description");
    }
}
