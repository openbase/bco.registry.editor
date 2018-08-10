package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import rst.domotic.service.ServiceDescriptionType.ServiceDescription;
import rst.domotic.service.ServiceDescriptionType.ServiceDescription.Builder;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;

import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ServiceDescriptionTreeItem extends BuilderTreeItem<ServiceDescription.Builder> {

    public ServiceDescriptionTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        addEventHandler(valueChangedEvent(), event -> {
            final GenericTreeItem source = (GenericTreeItem) event.getSource();
            try {
                if (source.getFieldDescriptor().getNumber() == ServiceDescription.SERVICE_TYPE_FIELD_NUMBER) {
                    final ServiceType serviceType = ServiceType.valueOf(((ValueType<EnumValueDescriptor>) event.getNewValue()).getValue().getNumber());
                    final String id = Registries.getTemplateRegistry().getServiceTemplateByType(serviceType).getId();
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
}
