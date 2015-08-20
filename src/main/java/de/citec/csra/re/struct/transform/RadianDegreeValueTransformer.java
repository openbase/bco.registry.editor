/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.transform;

import de.citec.jul.exception.CouldNotPerformException;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class RadianDegreeValueTransformer implements ValueTransformer {

    @Override
    public Object getValue(Object object) throws CouldNotPerformException {
        if (object instanceof Double) {
            Double value = (Double) object;
            return Math.toDegrees(value);
        }
        throw new CouldNotPerformException("Could not transform [" + object + "] to degree depiction");
    }

    @Override
    public Object setValue(Object object) throws CouldNotPerformException {
        if (object instanceof Double) {
            Double value = (Double) object;
            return Math.toRadians(value);
        }
        throw new CouldNotPerformException("Could not transform [" + object + "] to radian depiction");
    }

}
