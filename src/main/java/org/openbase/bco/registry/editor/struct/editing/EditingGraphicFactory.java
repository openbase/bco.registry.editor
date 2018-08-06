package org.openbase.bco.registry.editor.struct.editing;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
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

import javafx.scene.control.Control;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.jul.exception.CouldNotPerformException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class EditingGraphicFactory<V, EG extends AbstractEditingGraphic> {

    private static final Map<Class<? extends AbstractEditingGraphic>, EditingGraphicFactory> editingGraphicMap = new HashMap<>();

    public static synchronized <EG extends AbstractEditingGraphic> EditingGraphicFactory getInstance(final Class<EG> editingGraphicClass) throws CouldNotPerformException {
        if (editingGraphicMap.containsKey(editingGraphicClass)) {
            return editingGraphicMap.get(editingGraphicClass);
        }
        final EditingGraphicFactory editingGraphicFactory = new EditingGraphicFactory<>(editingGraphicClass);
        editingGraphicMap.put(editingGraphicClass, editingGraphicFactory);
        return editingGraphicFactory;
    }

    private final Constructor<EG> constructor;

    private EditingGraphicFactory(final Class<EG> editingGraphicClass) throws CouldNotPerformException {
        try {
            constructor = editingGraphicClass.getConstructor(ValueType.class, TreeTableCell.class);
        } catch (NoSuchMethodException ex) {
            throw new CouldNotPerformException("Could not retrieve constructor for editing graphic[" + editingGraphicClass.getSimpleName() + "]", ex);
        }
    }

    public Control getEditingGraphic(final ValueType<V> valueType, final TreeTableCell<ValueType<V>, ValueType<V>> cell) throws CouldNotPerformException {
        try {
            return constructor.newInstance(valueType, cell).getControl();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new CouldNotPerformException("Could not create new editing graphic", ex);
        }
    }
}
