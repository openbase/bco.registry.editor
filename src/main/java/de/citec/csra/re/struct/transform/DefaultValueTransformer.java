/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.transform;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class DefaultValueTransformer implements ValueTransformer{

    @Override
    public Object getValue(Object object) {
        return object;
    }

    @Override
    public Object setValue(Object object) {
        return object;
    }
    
}
