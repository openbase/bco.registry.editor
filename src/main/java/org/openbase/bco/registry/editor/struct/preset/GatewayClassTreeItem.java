package org.openbase.bco.registry.editor.struct.preset;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2022 openbase.org
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
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.RegistryMessageTreeItem;
import org.openbase.bco.registry.editor.struct.ValueListTreeItem;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.struct.editing.GatewayClassIdEditingGraphic;
import org.openbase.bco.registry.editor.struct.editing.LocationChildIdEditingGraphic;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.jul.extension.type.processing.ScopeProcessor;
import org.openbase.type.domotic.unit.gateway.GatewayClassType.GatewayClass;

public class GatewayClassTreeItem extends RegistryMessageTreeItem<GatewayClass.Builder> {

    public GatewayClassTreeItem(FieldDescriptor fieldDescriptor, GatewayClass.Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @Override
    protected GenericTreeItem createChild(FieldDescriptor field, Boolean editable) throws CouldNotPerformException {
        final GenericTreeItem child = super.createChild(field, editable);
        if (field.getNumber() == GatewayClass.NESTED_GATEWAY_CLASS_ID_FIELD_NUMBER) {
            ValueListTreeItem childIdTreeItem = (ValueListTreeItem) child;
            childIdTreeItem.setEditingGraphicFactory(EditingGraphicFactory.getInstance(GatewayClassIdEditingGraphic.class));
            childIdTreeItem.setDescriptionGenerator((DescriptionGenerator<String>) value -> {
                try {
                    return LabelProcessor.getBestMatch(Registries.getClassRegistry().getGatewayClassById(value).getLabel());
                } catch (CouldNotPerformException e) {
                    return value;
                }
            });
        }
        return child;
    }
}
