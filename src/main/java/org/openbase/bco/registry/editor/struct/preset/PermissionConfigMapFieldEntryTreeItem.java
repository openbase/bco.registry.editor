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

import com.google.protobuf.Descriptors.FieldDescriptor;
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.LeafTreeItem;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.struct.editing.PermissionGroupIdEditingGraphic;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.type.domotic.authentication.PermissionConfigType.PermissionConfig;
import org.openbase.type.domotic.authentication.PermissionConfigType.PermissionConfig.MapFieldEntry;
import org.openbase.type.domotic.authentication.PermissionConfigType.PermissionConfig.MapFieldEntry.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class PermissionConfigMapFieldEntryTreeItem extends BuilderTreeItem<PermissionConfig.MapFieldEntry.Builder> {

    public PermissionConfigMapFieldEntryTreeItem(final FieldDescriptor fieldDescriptor, final Builder builder, final Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @Override
    protected GenericTreeItem createChild(final FieldDescriptor field, final Boolean editable) throws CouldNotPerformException {
        switch (field.getNumber()) {
            case MapFieldEntry.GROUP_ID_FIELD_NUMBER:
                final LeafTreeItem<String> groupIdLeaf = new LeafTreeItem<>(field, getBuilder().getGroupId(), editable);
                groupIdLeaf.setEditingGraphicFactory(EditingGraphicFactory.getInstance(PermissionGroupIdEditingGraphic.class));
                groupIdLeaf.setDescriptionGenerator(value -> {
                    try {
                        return LabelProcessor.getBestMatch(Registries.getUnitRegistry().getUnitConfigById(value).getLabel());
                    } catch (CouldNotPerformException e) {
                        return value;
                    }
                });
                return groupIdLeaf;
            default:
                return super.createChild(field, editable);
        }
    }
}
