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
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.processing.StringProcessor;
import org.openbase.type.domotic.service.ServiceDescriptionType.ServiceDescription;
import org.openbase.type.domotic.service.ServiceDescriptionType.ServiceDescription.Builder;

import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ServiceDescriptionTreeItem extends BuilderTreeItem<ServiceDescription.Builder> {

    public ServiceDescriptionTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        addEventHandler(valueChangedEvent(), event -> {
            updateDescriptionGraphic();

            final GenericTreeItem source = (GenericTreeItem) event.getSource();
            try {
                if (source.getFieldDescriptor().getNumber() == ServiceDescription.SERVICE_TYPE_FIELD_NUMBER) {
                    final String id = Registries.getTemplateRegistry().getServiceTemplateByType(getBuilder().getServiceType()).getId();
                    for (FieldDescriptor descriptor : getDescriptorChildMap().keySet()) {
                        if (descriptor.getNumber() != ServiceDescription.SERVICE_TEMPLATE_ID_FIELD_NUMBER) {
                            continue;
                        }

                        getDescriptorChildMap().get(descriptor).update(id);
                    }

                }
            } catch (CouldNotPerformException ex) {
                logger.warn("Could not update service template id", ex);
            }
        });
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        final Set<Integer> uneditableFields = super.getUneditableFields();
        uneditableFields.add(ServiceDescription.SERVICE_TEMPLATE_ID_FIELD_NUMBER);
        return uneditableFields;
    }

    @Override
    protected String createDescriptionText() {
        return StringProcessor.transformUpperCaseToPascalCase(getBuilder().getServiceType().name().replace(
                "SERVICE", getBuilder().getPattern().name() + "_" + "SERVICE"));
    }
}
