package org.openbase.bco.registry.editor.struct.editing;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2019 openbase.org
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
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.type.domotic.unit.app.AppClassType.AppClass;

import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class AppClassIdEditingGraphic extends AbstractMessageEditingGraphic<AppClass> {

    public AppClassIdEditingGraphic(ValueType<String> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(value -> {
            try {
                return LabelProcessor.getBestMatch(value.getLabel());
            } catch (NotAvailableException e) {
                return value.getId();
            }
        }, valueType, treeTableCell);
    }

    @Override
    protected List<AppClass> getMessages() throws CouldNotPerformException {
        return Registries.getClassRegistry().getAppClasses();
    }

    @Override
    protected String getCurrentValue(final AppClass message) {
        return message.getId();
    }

    @Override
    protected AppClass getMessage(final String value) throws CouldNotPerformException {
        return Registries.getClassRegistry().getAppClassById(value);
    }
}
