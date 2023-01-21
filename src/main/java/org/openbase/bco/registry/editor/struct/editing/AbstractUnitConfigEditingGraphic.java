package org.openbase.bco.registry.editor.struct.editing;

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

import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.bco.registry.editor.util.UnitStringGenerator;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.type.processing.ScopeProcessor;
import org.openbase.jul.extension.type.processing.ScopeProcessor;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractUnitConfigEditingGraphic extends AbstractMessageEditingGraphic<UnitConfig> {

    public AbstractUnitConfigEditingGraphic(final ValueType<String> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        this(unitConfig -> {
            try {
                return UnitStringGenerator.generateLocationChainStringRep(unitConfig);
            } catch (CouldNotPerformException ex) {
                return unitConfig.getId();
            }
        }, valueType, treeTableCell);
    }

    public AbstractUnitConfigEditingGraphic(final DescriptionGenerator<UnitConfig> descriptionGenerator,
                                            final ValueType<String> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(descriptionGenerator, valueType, treeTableCell);
    }

    @Override
    protected String getCurrentValue(final UnitConfig unitConfig) {
        return unitConfig.getId();
    }

    @Override
    protected UnitConfig getMessage(final String unitId) throws CouldNotPerformException {
        return Registries.getUnitRegistry().getUnitConfigById(unitId);
    }
}
