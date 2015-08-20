/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.transform;

import de.citec.jul.exception.CouldNotPerformException;

/**
 * Visual value in degree and internal value in radians
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class RadianDegreeValueTransformer implements ValueTransformer {

    @Override
    public Object transformToVisual(Object object) throws CouldNotPerformException {
        if (object instanceof Double) {
            Double value = (Double) object;
            return Math.toDegrees(value);
        }
        throw new CouldNotPerformException("Could not transform [" + object + "] to degree depiction");
    }

    @Override
    public Object transformToInternal(Object object) throws CouldNotPerformException {
        if (object instanceof Double) {
            Double value = (Double) object;
            return Math.toRadians(value);
        }
        throw new CouldNotPerformException("Could not transform [" + object + "] to radian depiction");
    }

}
