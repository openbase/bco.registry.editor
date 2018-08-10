package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.FieldDescriptor;
import org.openbase.bco.registry.editor.struct.RegistryMessageTreeItem;
import org.openbase.jul.exception.InitializationException;
import rst.domotic.unit.UnitTemplateType.UnitTemplate;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.Builder;

import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class UnitTemplateTreeItem extends RegistryMessageTreeItem<UnitTemplate.Builder> {

    public UnitTemplateTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        final Set<Integer> uneditableFields = super.getUneditableFields();
        uneditableFields.add(UnitTemplate.TYPE_FIELD_NUMBER);
        return uneditableFields;
    }
}
