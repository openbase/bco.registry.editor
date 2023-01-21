package org.openbase.bco.registry.editor.util;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2023 openbase.org
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
import org.openbase.jul.extension.type.processing.ScopeProcessor;
import org.openbase.jul.extension.type.processing.ScopeProcessor;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class UnitScopeDescriptionGenerator implements DescriptionGenerator<String> {

    @Override
    public String getDescription(final String unitId) {
        try {
            return ScopeProcessor.generateStringRep(Registries.getUnitRegistry().getUnitConfigById(unitId).getScope());
        } catch (CouldNotPerformException ex) {
            return unitId;
        }
    }
}
