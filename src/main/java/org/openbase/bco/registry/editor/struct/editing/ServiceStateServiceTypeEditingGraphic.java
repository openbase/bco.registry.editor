package org.openbase.bco.registry.editor.struct.editing;

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

import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.preset.ServiceStateDescriptionTreeItem;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.type.domotic.service.ServiceDescriptionType.ServiceDescription;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServicePattern;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Special editing graphic for services types only displaying operation services of the unit type of a service
 * state description.
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ServiceStateServiceTypeEditingGraphic extends EnumEditingGraphicAll {

    /**
     * {@inheritDoc}
     *
     * @param valueType     {@inheritDoc}
     * @param treeTableCell {@inheritDoc}
     */
    public ServiceStateServiceTypeEditingGraphic(ValueType<EnumValueDescriptor> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(valueType, treeTableCell);
    }

    /**
     * Create a sorted list of operation service types of the unit type set in the service state description.
     *
     * @param enumDescriptor descriptor for the service type enum.
     *
     * @return a sorted list of operation service types of the unit type set in the service state description.
     */
    @Override
    protected ObservableList<EnumValueDescriptor> createSortedEnumList(final EnumDescriptor enumDescriptor) {
        // return a set of operation services of the selected unit type
        try {
            final ServiceStateDescriptionTreeItem parent = (ServiceStateDescriptionTreeItem) getValueType().getTreeItem().getParent();
            final List<EnumValueDescriptor> valueList = new ArrayList<>();

            if (parent.getBuilder().getUnitType() != UnitType.UNKNOWN) {
                final UnitTemplate unitTemplate = Registries.getTemplateRegistry().getUnitTemplateByType(parent.getBuilder().getUnitType());

                for (final ServiceDescription serviceDescription : unitTemplate.getServiceDescriptionList()) {
                    if (serviceDescription.getPattern() == ServicePattern.OPERATION) {
                        valueList.add(serviceDescription.getServiceType().getValueDescriptor());
                    }
                }
            } else {
                // if unit type is unknown retrieve all available service types
                for (UnitTemplate unitTemplate : Registries.getTemplateRegistry().getUnitTemplates()) {
                    for (ServiceDescription serviceDescription : unitTemplate.getServiceDescriptionList()) {
                        if (serviceDescription.getPattern() == ServicePattern.OPERATION && !valueList.contains(serviceDescription.getServiceType().getValueDescriptor())) {
                            valueList.add(serviceDescription.getServiceType().getValueDescriptor());
                        }
                    }
                }
            }
            valueList.sort(Comparator.comparing(EnumValueDescriptor::getName));
            return FXCollections.observableArrayList(valueList);
        } catch (CouldNotPerformException ex) {
            return super.createSortedEnumList(enumDescriptor);
        }
    }
}
