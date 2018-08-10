package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.FieldDescriptor;
import org.openbase.bco.registry.editor.struct.RegistryMessageTreeItem;
import org.openbase.jul.exception.InitializationException;
import rst.domotic.activity.ActivityTemplateType.ActivityTemplate;
import rst.domotic.activity.ActivityTemplateType.ActivityTemplate.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ActivityTemplateTreeItem extends RegistryMessageTreeItem<ActivityTemplate.Builder> {

    public ActivityTemplateTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }
}
