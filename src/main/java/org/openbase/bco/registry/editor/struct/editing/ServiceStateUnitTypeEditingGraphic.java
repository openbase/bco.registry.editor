package org.openbase.bco.registry.editor.struct.editing;

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

import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import rst.domotic.service.ServiceDescriptionType.ServiceDescription;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate.ServicePattern;
import rst.domotic.unit.UnitTemplateType.UnitTemplate;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Special editing graphic for unit types only displaying types with at least one operation service.
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ServiceStateUnitTypeEditingGraphic extends EnumEditingGraphic {

    /**
     * {@inheritDoc}
     *
     * @param valueType     {@inheritDoc}
     * @param treeTableCell {@inheritDoc}
     */
    public ServiceStateUnitTypeEditingGraphic(final ValueType<EnumValueDescriptor> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(valueType, treeTableCell);
    }

    /**
     * Create a sorted list of unit types with at least one operation service.
     *
     * @param enumDescriptor descriptor for the unit type enum.
     *
     * @return a sorted list of enum value descriptors of units with at least one operation service.
     */
    @Override
    protected ObservableList<EnumValueDescriptor> createSortedEnumList(final EnumDescriptor enumDescriptor) {
        // filter all unit types that do not provide at least one operation service and add unknown
        try {
            final List<EnumValueDescriptor> valueList = new ArrayList<>();
            valueList.add(UnitType.UNKNOWN.getValueDescriptor());
            outer:
            for (UnitTemplate unitTemplate : Registries.getTemplateRegistry().getUnitTemplates()) {
                for (ServiceDescription serviceDescription : unitTemplate.getServiceDescriptionList()) {
                    if (serviceDescription.getPattern() == ServicePattern.OPERATION) {
                        valueList.add(unitTemplate.getType().getValueDescriptor());
                        continue outer;
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
