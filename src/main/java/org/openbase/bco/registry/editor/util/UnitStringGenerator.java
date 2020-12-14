package org.openbase.bco.registry.editor.util;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2020 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.jul.processing.StringProcessor;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

public class UnitStringGenerator {

    /**
     * Generates a string representation of the given unit config that describes the hierarchy of this unit.
     * <p>
     * Note: Default delimiter is " > ":
     *
     * @param unitConfig the unit used as baseline.
     *
     * @return the string representation.
     */
    public static String generateLocationChainStringRep(final UnitConfig unitConfig) throws CouldNotPerformException {
        return generateLocationChainStringRep(unitConfig, " | ");
    }

    /**
     * Generates a string representation of the given unit config that describes the hierarchy of this unit.
     * e.g.: Home > Living > Ceiling [Light]
     *
     * @param unitConfig the unit used as baseline.
     * @param delimiter  the limiter used between unit and its placement.
     *
     * @return the string representation.
     */
    public static String generateLocationChainStringRep(UnitConfig unitConfig, final String delimiter) throws CouldNotPerformException {

        String rep = "";

        while (true) {

            // add element
            rep = LabelProcessor.getBestMatch(unitConfig.getLabel(), "?")
                    + (rep.isEmpty() ? "" : delimiter)
                    + rep;

            // add unit type
            if (unitConfig.getUnitType() != UnitType.LOCATION) {
                rep += " [" + StringProcessor.transformUpperCaseToPascalCase(unitConfig.getUnitType().name()) + "]";
            }

            // check termination
            if (unitConfig.getLocationConfig().getRoot()) {
                break;
            }

            unitConfig = Registries.getUnitRegistry().getUnitConfigByIdAndUnitType(unitConfig.getPlacementConfig().getLocationId(), UnitType.LOCATION);
        }
        return rep;
    }
}
