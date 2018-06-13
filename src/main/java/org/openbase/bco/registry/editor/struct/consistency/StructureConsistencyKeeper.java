package org.openbase.bco.registry.editor.struct.consistency;

/*
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

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import org.openbase.bco.registry.editor.struct.*;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.extension.rst.processing.TimestampProcessor;
import org.slf4j.LoggerFactory;
import rst.domotic.service.ServiceConfigType.ServiceConfig;
import rst.domotic.service.ServiceDescriptionType.ServiceDescription;
import rst.domotic.service.ServiceTemplateConfigType.ServiceTemplateConfig;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import rst.domotic.state.InventoryStateType.InventoryState;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateConfigType.UnitTemplateConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import rst.domotic.unit.unitgroup.UnitGroupConfigType.UnitGroupConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class StructureConsistencyKeeper {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(StructureConsistencyKeeper.class);

    public static void keepStructure(NodeContainer<? extends Message.Builder> container, String fieldName) throws CouldNotPerformException, InterruptedException {
        if (container.getBuilder() instanceof InventoryState.Builder) {
            keepInventoryStateStructure((NodeContainer<InventoryState.Builder>) container);
        } else if (container.getBuilder() instanceof UnitTemplateConfig.Builder) {
            keepUnitTemplateConfigStructure((NodeContainer<UnitTemplateConfig.Builder>) container, fieldName);
        } else if (container.getBuilder() instanceof UnitGroupConfig.Builder) {
            if (container instanceof GenericListContainer) {
                keepUnitGroupConfigStructure(((GenericNodeContainer<UnitGroupConfig.Builder>) container.getParent().getValue()), fieldName);
            } else {
                keepUnitGroupConfigStructure(((GenericNodeContainer<UnitGroupConfig.Builder>) container), fieldName);
            }
        }
    }

    public static void clearField(NodeContainer<? extends Message.Builder> container, String fieldName) {
        GeneratedMessage.Builder builder = container.getBuilder();
        for (int i = 0; i < container.getChildren().size(); i++) {
            Node item = container.getChildren().get(i).getValue();
            if (item instanceof NodeContainer && ((NodeContainer) item).getDescriptor().equals(fieldName)) {
                container.getChildren().remove(i);
                i--;
            } else if (item instanceof LeafContainer && ((LeafContainer) item).getDescriptor().equals(fieldName)) {
                container.getChildren().remove(i);
                i--;
            }
        }
        builder.clearField(ProtoBufFieldProcessor.getFieldDescriptor(builder, fieldName));
    }

    private static void keepUnitTemplateConfigStructure(NodeContainer<UnitTemplateConfig.Builder> container, String changedField) throws CouldNotPerformException, InstantiationException, InterruptedException {
        // check if the right field has been set
        if ("type".equals(changedField)) {
            // clear the field in the builder and remove all child tree items representing these
            StructureConsistencyKeeper.clearField(container, "service_template_config");

            // filter so that every serviceType is only added once
            Map<String, ServiceType> serviceTypeMap = new HashMap();
            for (ServiceDescription serviceDescription : Registries.getTemplateRegistry().getUnitTemplateByType(container.getBuilder().getType()).getServiceDescriptionList()) {
                serviceTypeMap.put(serviceDescription.getServiceType().toString(), serviceDescription.getServiceType());
            }

            // create the new values for the field and add them to the builder
            serviceTypeMap.values().stream().map((serviceType) -> ServiceTemplateConfig.newBuilder().setServiceType(serviceType)).forEach((serviceTemplateConfigBuilder) -> {
                container.getBuilder().addServiceTemplateConfig(serviceTemplateConfigBuilder);
            });

            // create and add a new child node container representing these children
            Descriptors.FieldDescriptor field = ProtoBufFieldProcessor.getFieldDescriptor(container.getBuilder(), UnitTemplateConfig.SERVICE_TEMPLATE_CONFIG_FIELD_NUMBER);
            container.add(new GenericListContainer(field, container.getBuilder()));
        }
    }

    private static void keepInventoryStateStructure(NodeContainer<InventoryState.Builder> container) throws CouldNotPerformException {
        // clear the field in the builder and remove all child tree items representing these
        StructureConsistencyKeeper.clearField(container, "timestamp");

        // create the new values for the field and add them to the builder
        TimestampProcessor.updateTimestampWithCurrentTime(container.getBuilder(), logger);

        // create and add a new child node container representing these children
        Descriptors.FieldDescriptor field = ProtoBufFieldProcessor.getFieldDescriptor(container.getBuilder(), InventoryState.TIMESTAMP_FIELD_NUMBER);
        container.add(new GenericNodeContainer<>(field, container.getBuilder().getTimestampBuilder()));
    }

    //unit group configs ... if service type changes set unit type unknown,
    //                       if unit type changes set service types,
    //                       in all cases remove incongrous member ids
    private static void keepUnitGroupConfigStructure(GenericNodeContainer<UnitGroupConfig.Builder> container, String changedField) throws CouldNotPerformException, InterruptedException {
        Descriptors.FieldDescriptor field;
        boolean change = false;
        if ("service_type".equals(changedField)) {
            change = true;
            StructureConsistencyKeeper.clearField(container, "unit_type");
            container.getBuilder().setUnitType(UnitType.UNKNOWN);
            field = ProtoBufFieldProcessor.getFieldDescriptor(container.getBuilder(), UnitGroupConfig.UNIT_TYPE_FIELD_NUMBER);
            container.registerElement(field.getName(), container.getBuilder().getField(field));
        } else if ("unit_type".equals(changedField)) {
            change = true;
            StructureConsistencyKeeper.clearField(container, "service_template");
            container.getBuilder().addAllServiceDescription(Registries.getTemplateRegistry().getUnitTemplateByType(container.getBuilder().getUnitType()).getServiceDescriptionList());
            field = ProtoBufFieldProcessor.getFieldDescriptor(container.getBuilder(), UnitGroupConfig.SERVICE_DESCRIPTION_FIELD_NUMBER);
            container.add(new GenericListContainer<>(field, container.getBuilder()));
        }

        if (change) {
            List<String> memberIds = new ArrayList<>();
            if (container.getBuilder().getUnitType() == UnitType.UNKNOWN) {
                //check if every unit hast all given services
                for (String memberId : container.getBuilder().getMemberIdList()) {
                    UnitConfig unitConfig = Registries.getUnitRegistry().getUnitConfigById(memberId);
                    boolean skip = false;
                    for (ServiceConfig serviceConfig : unitConfig.getServiceConfigList()) {
                        if (!container.getBuilder().getServiceDescriptionList().contains(serviceConfig.getServiceDescription())) {
                            skip = true;
                        }
                    }
                    if (skip) {
                        continue;
                    }
                    memberIds.add(memberId);
                }
            } else {
                for (String memberId : container.getBuilder().getMemberIdList()) {
                    UnitType unitType = Registries.getUnitRegistry().getUnitConfigById(memberId).getUnitType();
                    if (unitType == container.getBuilder().getUnitType() || Registries.getTemplateRegistry().getSubUnitTypes(container.getBuilder().getUnitType()).contains(unitType)) {
                        memberIds.add(memberId);
                    }
                }
            }
            StructureConsistencyKeeper.clearField(container, "member_id");
            container.getBuilder().addAllMemberId(memberIds);
            field = ProtoBufFieldProcessor.getFieldDescriptor(container.getBuilder(), UnitGroupConfig.MEMBER_ID_FIELD_NUMBER);
            container.add(new GenericListContainer<>(field, container.getBuilder()));
        }
    }
}
