package org.openbase.bco.registry.editor.struct.preset;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2023 openbase.org
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
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.type.domotic.service.ServiceDescriptionType.ServiceDescription;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import org.openbase.type.domotic.unit.UnitTemplateConfigType.UnitTemplateConfig;
import org.openbase.type.domotic.unit.UnitTemplateConfigType.UnitTemplateConfig.Builder;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class UnitTemplateConfigTreeItem extends BuilderTreeItem<UnitTemplateConfig.Builder> {

    public UnitTemplateConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        addEventHandler(valueChangedEvent(), event -> {
            updateDescriptionGraphic();

            // do nothing if not a direct child has been modified
            if (!event.getSource().getParent().equals(this)) {
                return;
            }

            final GenericTreeItem source = (GenericTreeItem) event.getSource();
            if (source.getFieldDescriptor().getNumber() == UnitTemplateConfig.UNIT_TYPE_FIELD_NUMBER) {
                try {
                    // filter so that every serviceType is only added once
                    final Set<ServiceType> serviceTypeSet = new HashSet<>();
                    for (final ServiceDescription serviceDescription :
                            Registries.getTemplateRegistry().getUnitTemplateByType(getBuilder().getUnitType()).getServiceDescriptionList()) {
                        serviceTypeSet.add(serviceDescription.getServiceType());
                    }

                    // retrieve service template config field
                    FieldDescriptor field = ProtoBufFieldProcessor.getFieldDescriptor(getBuilder(), UnitTemplateConfig.SERVICE_TEMPLATE_CONFIG_FIELD_NUMBER);

                    // clear current service template
                    getBuilder().clearField(field);

                    // add new service template configs
                    for (ServiceType serviceType : serviceTypeSet) {
                        getBuilder().addServiceTemplateConfigBuilder().setServiceType(serviceType);
                    }

                    // update according child
                    getDescriptorChildMap().get(field).update(getBuilder());
                } catch (CouldNotPerformException ex) {
                    logger.warn("Could not update service template configs", ex);
                }
            }
        });
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        final Set<Integer> uneditableFields = super.getUneditableFields();
        uneditableFields.add(UnitTemplateConfig.ID_FIELD_NUMBER);
        uneditableFields.add(UnitTemplateConfig.SERVICE_TEMPLATE_CONFIG_FIELD_NUMBER);
        return uneditableFields;
    }

    @Override
    protected String createDescriptionText() {
        try {
            return LabelProcessor.getBestMatch(getBuilder().getLabel());
        } catch (NotAvailableException ex) {
            return super.createDescriptionText();
        }
    }
}
