package org.openbase.bco.registry.editor.util;

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
