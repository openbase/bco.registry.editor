package org.openbase.bco.registry.editor.struct.preset;

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

import com.google.protobuf.Descriptors.FieldDescriptor;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.LeafTreeItem;
import org.openbase.bco.registry.editor.struct.RegistryMessageTreeItem;
import org.openbase.bco.registry.editor.struct.editing.ActivityTemplateIdEditingGraphic;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import rst.domotic.activity.ActivityConfigType.ActivityConfig;
import rst.domotic.activity.ActivityConfigType.ActivityConfig.Builder;

import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ActivityConfigTreeItem extends RegistryMessageTreeItem<ActivityConfig.Builder> {

    public ActivityConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected GenericTreeItem createChild(final FieldDescriptor field, final Boolean editable) throws CouldNotPerformException {
        GenericTreeItem child;
        if (field.getNumber() == ActivityConfig.ACTIVITY_TEMPLATE_ID_FIELD_NUMBER) {
            final LeafTreeItem leaf = new LeafTreeItem<>(field, getBuilder().getActivityTemplateId(), editable);
            leaf.setEditingGraphicFactory(EditingGraphicFactory.getInstance(ActivityTemplateIdEditingGraphic.class));
            leaf.setDescriptionGenerator((DescriptionGenerator<String>) value -> {
                try {
                    return LabelProcessor.getBestMatch(Registries.getClassRegistry().getDeviceClassById(value).getLabel());
                } catch (CouldNotPerformException e) {
                    return value;
                }
            });
            child = leaf;
        } else {
            child = super.createChild(field, editable);
        }
        return child;
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        Set<Integer> uneditableFields = super.getUneditableFields();
        if (!getBuilder().getActivityTemplateId().isEmpty()) {
            uneditableFields.add(ActivityConfig.ACTIVITY_TEMPLATE_ID_FIELD_NUMBER);
        }
        return uneditableFields;
    }
}
