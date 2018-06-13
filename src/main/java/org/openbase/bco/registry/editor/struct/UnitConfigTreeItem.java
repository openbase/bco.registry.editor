package org.openbase.bco.registry.editor.struct;

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
import javafx.beans.property.SimpleObjectProperty;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitConfigType.UnitConfig.Builder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class UnitConfigTreeItem extends BuilderTreeItem<UnitConfig.Builder> {

    private final SimpleObjectProperty<Boolean> changedProperty;

    public UnitConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder) {
        super(fieldDescriptor, builder);

        this.changedProperty = new SimpleObjectProperty<>(false);

        this.addEventHandler(valueChangedEvent(), event -> {
            // this is triggered when the value of this node or one of its children changes
            changedProperty.set(true);
        });

        this.addEventHandler(childrenModificationEvent(), event -> {
            // this is triggered when children are removed or added
            changedProperty.set(true);
        });
    }

//    @Override
//    public String getDescription() {
//        try {
//            return LabelProcessor.getFirstLabel(getValue().getBuilder().getLabel());
//        } catch (NotAvailableException ex) {
//            logger.warn("Unit[" + getValue().getBuilder().getAlias(0) + "] without a label", ex);
//            return super.getDescription();
//        }
//    }

    public SimpleObjectProperty<Boolean> getChangedProperty() {
        return changedProperty;
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
                UnitConfig.OBJECT_CONFIG_FIELD_NUMBER));

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