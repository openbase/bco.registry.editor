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
public class UnitTemplateContainer extends VariableNode<UnitTemplate.Builder>{

    public UnitTemplateContainer(UnitTemplate.Builder unitTemplate) {
        super("unit", unitTemplate);
        super.add(unitTemplate.getType(), "type");
        super.add(this);
    }
    
}
