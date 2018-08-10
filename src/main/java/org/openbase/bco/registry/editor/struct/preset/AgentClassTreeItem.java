package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.FieldDescriptor;
import org.openbase.bco.registry.editor.struct.RegistryMessageTreeItem;
import org.openbase.jul.exception.InitializationException;
import rst.domotic.unit.agent.AgentClassType.AgentClass;
import rst.domotic.unit.agent.AgentClassType.AgentClass.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class AgentClassTreeItem extends RegistryMessageTreeItem<AgentClass.Builder> {

    public AgentClassTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }
}
