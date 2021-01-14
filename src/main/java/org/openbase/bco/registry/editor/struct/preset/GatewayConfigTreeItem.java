package org.openbase.bco.registry.editor.struct.preset;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2021 openbase.org
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

import com.google.protobuf.Descriptors.FieldDescriptor;
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.LeafTreeItem;
import org.openbase.bco.registry.editor.struct.ValueListTreeItem;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.struct.editing.GatewayClassIdEditingGraphic;
import org.openbase.bco.registry.editor.struct.editing.NestedGatewayIdEditingGraphic;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.type.domotic.unit.gateway.GatewayClassType;
import org.openbase.type.domotic.unit.gateway.GatewayConfigType.GatewayConfig;
import org.openbase.type.domotic.unit.gateway.GatewayConfigType.GatewayConfig.Builder;

import java.util.Set;

public class GatewayConfigTreeItem extends BuilderTreeItem<GatewayConfig.Builder> {

    public GatewayConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected GenericTreeItem createChild(FieldDescriptor field, Boolean editable) throws CouldNotPerformException {
        //GenericTreeItem child;
        switch (field.getNumber()) {
            case GatewayConfig.GATEWAY_CLASS_ID_FIELD_NUMBER:
                final LeafTreeItem childClassID = new LeafTreeItem<>(field, getBuilder().getGatewayClassId(), editable);
                childClassID.setEditingGraphicFactory(EditingGraphicFactory.getInstance(GatewayClassIdEditingGraphic.class));
                childClassID.setDescriptionGenerator((DescriptionGenerator<String>) value -> {
                    try {
                        return LabelProcessor.getBestMatch(Registries.getClassRegistry().getGatewayClassById(value).getLabel());
                    } catch (CouldNotPerformException e) {
                        return value;
                    }
                });
                return childClassID;
            case GatewayConfig.NESTED_GATEWAY_ID_FIELD_NUMBER:
                ValueListTreeItem childGatewayId = (ValueListTreeItem) super.createChild(field, editable);
                childGatewayId.setEditingGraphicFactory(EditingGraphicFactory.getInstance(NestedGatewayIdEditingGraphic.class));
                childGatewayId.setDescriptionGenerator((DescriptionGenerator<String>) value -> {
                    try {
                        return LabelProcessor.getBestMatch(Registries.getUnitRegistry().getUnitConfigById(value).getLabel());
                    } catch (CouldNotPerformException e) {
                        return value;
                    }
                });
                return childGatewayId;
            default:
                return super.createChild(field, editable);
        }
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        Set<Integer> uneditableFields = super.getUneditableFields();
        uneditableFields.add(GatewayConfig.UNIT_ID_FIELD_NUMBER);
        if (!getBuilder().getGatewayClassId().isEmpty()) {
            uneditableFields.add(GatewayConfig.GATEWAY_CLASS_ID_FIELD_NUMBER);
        }
        return uneditableFields;
    }
}
