package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.FieldDescriptor;
import org.openbase.bco.registry.editor.struct.RegistryMessageTreeItem;
import org.openbase.jul.exception.InitializationException;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate.Builder;

import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ServiceTemplateTreeItem extends RegistryMessageTreeItem<ServiceTemplate.Builder> {

    public ServiceTemplateTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        final Set<Integer> filteredFields = super.getUneditableFields();
        filteredFields.add(ServiceTemplate.TYPE_FIELD_NUMBER);
        return filteredFields;
    }
}
