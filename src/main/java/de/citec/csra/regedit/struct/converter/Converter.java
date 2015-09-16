/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.struct.converter;

import de.citec.jul.exception.CouldNotPerformException;
import java.util.Map;

/**
 *
 * 
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public interface Converter {
    
    //TODO Tamnio: Why is this converter not generic? -> Converter<V> -> updateBuilder(String fieldName, V value)
    public void updateBuilder(String fieldName, Object value) throws CouldNotPerformException;

    public Map<String, Object> getFields();
}
