/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.transform;

import de.citec.jul.exception.CouldNotPerformException;

/**
 * Interface for a transformer to differentiate between the internal value and
 * and the visual representation. E.g. used to display radian values in degree
 * end allow editing them in degree.
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public interface ValueTransformer {

    /**
     * Get the visual representation.
     *
     * @param object the value that will be transformed
     * @return the transformed value
     * @throws CouldNotPerformException thrown if the transformation cannot be
     * performed on such objects
     */
    Object transformToVisual(Object object) throws CouldNotPerformException;

    /**
     * Get the internal value.
     *
     * @param object the visual value that will be transformed and then set
     * @return the transformed value
     * @throws CouldNotPerformException thrown if the transformation cannot be
     * performed on such objects
     */
    Object transformToInternal(Object object) throws CouldNotPerformException;
}
