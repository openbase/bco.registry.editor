package org.openbase.bco.registry.editor.struct.preset;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2020 openbase.org
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
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openbase.bco.registry.editor.struct.BuilderListTreeItem;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.RegistryMessageTreeItem;
import org.openbase.bco.registry.editor.struct.ValueListTreeItem;
import org.openbase.bco.registry.editor.struct.editing.AuthorizationGroupMemberEditingGraphic;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.util.SelectableLabel;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.type.processing.MultiLanguageTextProcessor;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig.Builder;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class UnitConfigTreeItem extends RegistryMessageTreeItem<Builder> {

    public UnitConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @Override
    protected Node createValueGraphic() {
        final Node valueGraphic = super.createValueGraphic();
        if (valueGraphic == null) {
            try {
                return SelectableLabel.makeSelectable(new Label(MultiLanguageTextProcessor.getBestMatch(getBuilder().getDescription())));
            } catch (NotAvailableException e) {
                return SelectableLabel.makeSelectable(new Label());
            }
        }
        return valueGraphic;
    }

    @Override
    protected GenericTreeItem createChild(final FieldDescriptor field, final Boolean editable) throws CouldNotPerformException {
        switch (field.getNumber()) {
            case UnitConfig.SERVICE_CONFIG_FIELD_NUMBER:
                return new BuilderListTreeItem<>(field, getBuilder(), false);
            case UnitConfig.AUTHORIZATION_GROUP_CONFIG_FIELD_NUMBER:
                final ValueListTreeItem child = (ValueListTreeItem) super.createChild(field, editable);
                child.setDescriptionText("MemberIdList");
                child.setEditingGraphicFactory(EditingGraphicFactory.getInstance(AuthorizationGroupMemberEditingGraphic.class));
                return child;
            default:
                return super.createChild(field, editable);
        }
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        final Set<Integer> uneditableFields = super.getUneditableFields();
        uneditableFields.add(UnitConfig.UNIT_HOST_ID_FIELD_NUMBER);
        uneditableFields.add(UnitConfig.UNIT_TEMPLATE_CONFIG_ID_FIELD_NUMBER);
        if (getBuilder().getUnitType() != UnitType.UNKNOWN) {
            uneditableFields.add(UnitConfig.UNIT_TYPE_FIELD_NUMBER);
        }
        uneditableFields.add(UnitConfig.SERVICE_CONFIG_FIELD_NUMBER);
        uneditableFields.add(UnitConfig.SCOPE_FIELD_NUMBER);
        return uneditableFields;
    }

    @Override
    protected Set<Integer> getFilteredFields() {
        // create default set for dal units
        final Set<Integer> filteredFieldSet = new HashSet<>(Arrays.asList(
                UnitConfig.AGENT_CONFIG_FIELD_NUMBER,
                UnitConfig.APP_CONFIG_FIELD_NUMBER,
                UnitConfig.AUTHORIZATION_GROUP_CONFIG_FIELD_NUMBER,
                UnitConfig.CONNECTION_CONFIG_FIELD_NUMBER,
                UnitConfig.DEVICE_CONFIG_FIELD_NUMBER,
                UnitConfig.LOCATION_CONFIG_FIELD_NUMBER,
                UnitConfig.SCENE_CONFIG_FIELD_NUMBER,
                UnitConfig.UNIT_GROUP_CONFIG_FIELD_NUMBER,
                UnitConfig.USER_CONFIG_FIELD_NUMBER,
                UnitConfig.OBJECT_CONFIG_FIELD_NUMBER,
                UnitConfig.GATEWAY_CONFIG_FIELD_NUMBER));

        // remove filtered fields specified by unit type and save if it is a dal unit
        boolean isDalUnit = false;
        switch (getBuilder().getUnitType()) {
            case AGENT:
                filteredFieldSet.remove(UnitConfig.AGENT_CONFIG_FIELD_NUMBER);
                break;
            case APP:
                filteredFieldSet.remove(UnitConfig.APP_CONFIG_FIELD_NUMBER);
                break;
            case AUTHORIZATION_GROUP:
                filteredFieldSet.remove(UnitConfig.AUTHORIZATION_GROUP_CONFIG_FIELD_NUMBER);
                break;
            case CONNECTION:
                filteredFieldSet.remove(UnitConfig.CONNECTION_CONFIG_FIELD_NUMBER);
                break;
            case DEVICE:
                filteredFieldSet.remove(UnitConfig.DEVICE_CONFIG_FIELD_NUMBER);
                break;
            case LOCATION:
                filteredFieldSet.remove(UnitConfig.LOCATION_CONFIG_FIELD_NUMBER);
                break;
            case OBJECT:
                filteredFieldSet.remove(UnitConfig.OBJECT_CONFIG_FIELD_NUMBER);
                break;
            case SCENE:
                filteredFieldSet.remove(UnitConfig.SCENE_CONFIG_FIELD_NUMBER);
                break;
            case UNIT_GROUP:
                filteredFieldSet.remove(UnitConfig.UNIT_GROUP_CONFIG_FIELD_NUMBER);
                break;
            case USER:
                filteredFieldSet.remove(UnitConfig.USER_CONFIG_FIELD_NUMBER);
                break;
            case GATEWAY:
                filteredFieldSet.remove(UnitConfig.GATEWAY_CONFIG_FIELD_NUMBER);
                break;
            default:
                isDalUnit = true;

        }

        // if not a dal unit filter some more fields
        if (!isDalUnit) {
            filteredFieldSet.addAll(Arrays.asList(
                    UnitConfig.UNIT_TEMPLATE_CONFIG_ID_FIELD_NUMBER,
                    UnitConfig.BOUND_TO_UNIT_HOST_FIELD_NUMBER,
                    UnitConfig.UNIT_HOST_ID_FIELD_NUMBER));
        }

        return filteredFieldSet;
    }
}
