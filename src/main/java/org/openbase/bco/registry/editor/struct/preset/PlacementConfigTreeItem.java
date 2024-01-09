package org.openbase.bco.registry.editor.struct.preset;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2024 openbase.org
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
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.struct.editing.LocationIdEditingGraphic;
import org.openbase.bco.registry.editor.util.UnitStringGenerator;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.type.processing.ScopeProcessor;
import org.openbase.type.spatial.PlacementConfigType.PlacementConfig;
import org.openbase.type.spatial.PlacementConfigType.PlacementConfig.Builder;

import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class PlacementConfigTreeItem extends BuilderTreeItem<PlacementConfig.Builder> {

    public PlacementConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        final Set<Integer> uneditableFields = super.getUneditableFields();
        uneditableFields.add(PlacementConfig.TRANSFORMATION_FRAME_ID_FIELD_NUMBER);
        return uneditableFields;
    }

    @Override
    protected GenericTreeItem createChild(final FieldDescriptor field, final Boolean editable) throws CouldNotPerformException {
        switch (field.getNumber()) {
            case PlacementConfig.LOCATION_ID_FIELD_NUMBER:
                final LeafTreeItem<String> leafTreeItem = new LeafTreeItem<>(field, getBuilder().getLocationId(), editable);
                leafTreeItem.setDescriptionGenerator(value -> {
                    try {
                        return UnitStringGenerator.generateLocationChainStringRep(Registries.getUnitRegistry().getUnitConfigById(value));
                    } catch (CouldNotPerformException e) {
                        return value;
                    }
                });
                leafTreeItem.setEditingGraphicFactory(EditingGraphicFactory.getInstance(LocationIdEditingGraphic.class));
                return leafTreeItem;
            default:
                return super.createChild(field, editable);
        }
    }
}
