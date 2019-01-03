package org.openbase.bco.registry.editor.struct.preset;

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

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.ValueListTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.struct.editing.LocationChildIdEditingGraphic;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import org.openbase.type.domotic.unit.location.LocationConfigType.LocationConfig;
import org.openbase.type.domotic.unit.location.LocationConfigType.LocationConfig.Builder;
import org.openbase.type.domotic.unit.location.LocationConfigType.LocationConfig.LocationType;

import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LocationConfigTreeItem extends BuilderTreeItem<LocationConfig.Builder> {

    public LocationConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        addEventHandler(valueChangedEvent(), event -> {
            // if the location type has changed to tile display the tile config else remove it if it is there
            final GenericTreeItem source = (GenericTreeItem) event.getSource();
            if (source.getFieldDescriptor().getNumber() == LocationConfig.TYPE_FIELD_NUMBER) {
                final LocationType locationType = LocationType.valueOf(((ValueType<EnumValueDescriptor>) event.getNewValue()).getValue().getNumber());
                if (locationType == LocationType.TILE) {
                    FieldDescriptor fieldDescriptor1 = ProtoBufFieldProcessor.getFieldDescriptor(getBuilder(), LocationConfig.TILE_CONFIG_FIELD_NUMBER);
                    try {
                        GenericTreeItem child = createChild(fieldDescriptor1, true);
                        child.getChildren();
                        getChildren().add(child);
                        getDescriptorChildMap().put(fieldDescriptor1, child);
                    } catch (CouldNotPerformException ex) {
                        logger.error("Could not add tile as child", ex);
                    }
                } else {
                    FieldDescriptor toRemove = null;
                    for (FieldDescriptor descriptor : getDescriptorChildMap().keySet()) {
                        if (descriptor.getNumber() == LocationConfig.TILE_CONFIG_FIELD_NUMBER) {
                            toRemove = descriptor;
                            break;
                        }
                    }

                    if (toRemove == null) {
                        return;
                    }

                    GenericTreeItem treeItem = getDescriptorChildMap().get(toRemove);
                    getDescriptorChildMap().remove(toRemove);
                    getChildren().remove(treeItem);
                }
            }
        });
    }

    @Override
    protected GenericTreeItem createChild(FieldDescriptor field, Boolean editable) throws CouldNotPerformException {
        final GenericTreeItem child = super.createChild(field, editable);
        if (field.getNumber() == LocationConfig.CHILD_ID_FIELD_NUMBER) {
            ValueListTreeItem childIdTreeItem = (ValueListTreeItem) child;
            childIdTreeItem.setEditingGraphicFactory(EditingGraphicFactory.getInstance(LocationChildIdEditingGraphic.class));
            childIdTreeItem.setDescriptionGenerator(value -> {
                final String locationId = (String) value;
                try {
                    return ScopeGenerator.generateStringRep(Registries.getUnitRegistry().getUnitConfigById(locationId).getScope());
                } catch (CouldNotPerformException e) {
                    return locationId;
                }
            });
        }
        return child;
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        final Set<Integer> uneditableFields = super.getUneditableFields();
        uneditableFields.add(LocationConfig.UNIT_ID_FIELD_NUMBER);
        uneditableFields.add(LocationConfig.ROOT_FIELD_NUMBER);
        return uneditableFields;
    }

    @Override
    protected Set<Integer> getFilteredFields() {
        // only display the tile config if the location is of type tile
        final Set<Integer> filteredFields = super.getFilteredFields();
        if (getBuilder().getType() != LocationType.TILE) {
            filteredFields.add(LocationConfig.TILE_CONFIG_FIELD_NUMBER);
        }
        return filteredFields;
    }
}
