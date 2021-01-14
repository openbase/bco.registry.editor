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
import org.openbase.bco.registry.editor.struct.ValueListTreeItem;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.struct.editing.UnitGroupMemberEditingGraphic;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.bco.registry.editor.util.UnitStringGenerator;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.type.domotic.service.ServiceConfigType.ServiceConfig;
import org.openbase.type.domotic.service.ServiceDescriptionType.ServiceDescription;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import org.openbase.type.domotic.unit.unitgroup.UnitGroupConfigType.UnitGroupConfig;
import org.openbase.type.domotic.unit.unitgroup.UnitGroupConfigType.UnitGroupConfig.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class UnitGroupConfigTreeItem extends BuilderTreeItem<UnitGroupConfig.Builder> {

    private final FieldDescriptor unitTypeField;
    private final FieldDescriptor serviceDescriptionField;
    private final FieldDescriptor serviceTypeField;
    private final FieldDescriptor servicePatternField;

    public UnitGroupConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        unitTypeField = ProtoBufFieldProcessor.getFieldDescriptor(builder, UnitGroupConfig.UNIT_TYPE_FIELD_NUMBER);
        serviceDescriptionField = ProtoBufFieldProcessor.getFieldDescriptor(builder, UnitGroupConfig.SERVICE_DESCRIPTION_FIELD_NUMBER);
        serviceTypeField = ProtoBufFieldProcessor.getFieldDescriptor(ServiceDescription.getDefaultInstance(), ServiceDescription.SERVICE_TYPE_FIELD_NUMBER);
        servicePatternField = ProtoBufFieldProcessor.getFieldDescriptor(ServiceDescription.getDefaultInstance(), ServiceDescription.PATTERN_FIELD_NUMBER);

        addEventHandler(valueChangedEvent(), event -> {
            final GenericTreeItem<?> source = (GenericTreeItem) event.getSource();

            try {
                boolean updateMembers = false;
                if (source.getFieldDescriptor().getName().equals(unitTypeField.getName()) && getBuilder().getUnitType() != UnitType.UNKNOWN) {
                    GenericTreeItem treeItem = getDescriptorChildMap().get(serviceDescriptionField);
                    getBuilder().clearServiceDescription().addAllServiceDescription(
                            Registries.getTemplateRegistry().getUnitTemplateByType(getBuilder().getUnitType()).getServiceDescriptionList());
                    treeItem.update(getBuilder());
                    updateMembers = true;
                } else if (source.getFieldDescriptor().getName().equals(serviceTypeField.getName()) || source.getFieldDescriptor().getName().equals(servicePatternField.getName())) {
                    getBuilder().setUnitType(UnitType.UNKNOWN);
                    GenericTreeItem treeItem = getDescriptorChildMap().get(unitTypeField);
                    treeItem.update(getBuilder().getUnitType().getValueDescriptor());
                    updateMembers = true;
                }

                if (updateMembers) {
                    updateMemberIds();
                }
            } catch (CouldNotPerformException ex) {
                logger.warn("Could not update unit group config", ex);
            }
        });
    }

    private void updateMemberIds() throws CouldNotPerformException {
        List<String> memberIds = new ArrayList<>();
        if (getBuilder().getUnitType() == UnitType.UNKNOWN) {
            //check if every unit hast all given services
            for (String memberId : getBuilder().getMemberIdList()) {
                UnitConfig unitConfig = Registries.getUnitRegistry().getUnitConfigById(memberId);
                boolean skip = false;
                for (ServiceConfig serviceConfig : unitConfig.getServiceConfigList()) {
                    if (!getBuilder().getServiceDescriptionList().contains(serviceConfig.getServiceDescription())) {
                        skip = true;
                    }
                }
                if (skip) {
                    continue;
                }
                memberIds.add(memberId);
            }
        } else {
            final List<UnitType> subUnitTypes = Registries.getTemplateRegistry().getSubUnitTypes(getBuilder().getUnitType());
            for (final String memberId : getBuilder().getMemberIdList()) {
                UnitType unitType = Registries.getUnitRegistry().getUnitConfigById(memberId).getUnitType();
                if (unitType == getBuilder().getUnitType() || subUnitTypes.contains(unitType)) {
                    memberIds.add(memberId);
                }
            }
        }

        getBuilder().clearMemberId().addAllMemberId(memberIds);
        ValueListTreeItem<UnitGroupConfig.Builder> treeItem = (ValueListTreeItem<UnitGroupConfig.Builder>)
                getDescriptorChildMap().get(ProtoBufFieldProcessor.getFieldDescriptor(getBuilder(), UnitGroupConfig.MEMBER_ID_FIELD_NUMBER));
        treeItem.update(getBuilder());
    }

    @Override
    protected GenericTreeItem createChild(FieldDescriptor field, Boolean editable) throws CouldNotPerformException {
        if (field.getNumber() == UnitGroupConfig.MEMBER_ID_FIELD_NUMBER) {
            final ValueListTreeItem<Builder> valueListTreeItem = new ValueListTreeItem<>(field, getBuilder(), editable);
            valueListTreeItem.setEditingGraphicFactory(EditingGraphicFactory.getInstance(UnitGroupMemberEditingGraphic.class));
            valueListTreeItem.setDescriptionGenerator((DescriptionGenerator<String>) value -> {
                try {
                    return UnitStringGenerator.generateLocationChainStringRep(Registries.getUnitRegistry().getUnitConfigById(value));
                } catch (CouldNotPerformException e) {
                    return value;
                }
            });
            return valueListTreeItem;
        }
        return super.createChild(field, editable);
    }
}
