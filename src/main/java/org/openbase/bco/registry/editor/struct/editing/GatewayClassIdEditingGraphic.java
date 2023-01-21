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

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueListTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.preset.GatewayClassTreeItem;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.jul.pattern.ListFilter;
import org.openbase.type.domotic.unit.gateway.GatewayClassType.GatewayClass;

import java.util.ArrayList;
import java.util.List;

public class GatewayClassIdEditingGraphic extends AbstractMessageEditingGraphic<GatewayClass> {


    public GatewayClassIdEditingGraphic(ValueType<String> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(value -> {
            try {
                return LabelProcessor.getBestMatch(value.getLabel());
            } catch (NotAvailableException e) {
                return value.getId();
            }
        }, valueType, treeTableCell);
    }

    @Override
    protected List<GatewayClass> getMessages() throws CouldNotPerformException {
        final List<GatewayClass> gatewayClasses = Registries.getClassRegistry().getGatewayClasses();

        // if list (nested gateway ids) remove ids of gateway class and other children
        if (getValueType().getTreeItem().getParent() instanceof ValueListTreeItem) {
            final List<String> idsToRemove = new ArrayList<>();
            final ValueListTreeItem valueListTreeItem = (ValueListTreeItem) getValueType().getTreeItem().getParent();

            // remove ids of other gateway classes already nested
            for (final TreeItem child : (List<TreeItem>) valueListTreeItem.getChildren()) {
                final String id = ((ValueType<String>) child.getValue()).getValue();

                if (!id.isEmpty()) {
                    idsToRemove.add(id);
                }
            }

            // remove id of this gateway
            final GatewayClassTreeItem parent = (GatewayClassTreeItem) valueListTreeItem.getParent();
            idsToRemove.add(parent.getId());

            final ListFilter<GatewayClass> idFilter = gatewayClass -> idsToRemove.contains(gatewayClass.getId());
            idFilter.filter(gatewayClasses);
        }

        return gatewayClasses;
    }

    @Override
    protected String getCurrentValue(final GatewayClass message) {
        return message.getId();
    }

    @Override
    protected GatewayClass getMessage(String value) throws CouldNotPerformException {
        return Registries.getClassRegistry().getGatewayClassById(value);
    }
}
