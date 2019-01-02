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

import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.FocusedTextField;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import org.openbase.type.com.ScopeType.Scope;
import org.openbase.type.com.ScopeType.Scope.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ScopeEditingGraphic extends AbstractBuilderEditingGraphic<TextField, Builder> {

    public ScopeEditingGraphic(ValueType<Scope.Builder> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new FocusedTextField(), valueType, treeTableCell);
    }

    @Override
    protected boolean updateBuilder(Builder builder) {
        final List<String> newComponentList = new ArrayList<>();
        for (final String component : getControl().getText().split("/")) {
            if (component.isEmpty()) {
                // ignore empty components
                continue;
            }
            newComponentList.add(ScopeGenerator.convertIntoValidScopeComponent(component));
        }

        if (equalLabelList(newComponentList, builder.getComponentList())) {
            return false;
        }

        builder.clearComponent().addAllComponent(newComponentList);
        return true;
    }

    private boolean equalLabelList(final List<String> list1, final List<String> list2) {
        for (final String label : list1) {
            if (!list2.contains(label)) {
                return false;
            }
        }
        for (final String label : list2) {
            if (!list1.contains(label)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void init(Scope.Builder value) {
        try {
            getControl().setText(ScopeGenerator.generateStringRep(value.build()));
        } catch (CouldNotPerformException ex) {
            logger.error("Could not init scope editing graphic", ex);
        }
    }
}
