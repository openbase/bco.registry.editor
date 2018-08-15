package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.FieldDescriptor;
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.LeafTreeItem;
import org.openbase.bco.registry.editor.struct.editing.AgentClassIdEditingGraphic;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import rst.domotic.unit.agent.AgentConfigType.AgentConfig;
import rst.domotic.unit.agent.AgentConfigType.AgentConfig.Builder;

import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class AgentConfigTreeItem extends BuilderTreeItem<AgentConfig.Builder> {

    public AgentConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected GenericTreeItem createChild(FieldDescriptor field, Boolean editable) throws CouldNotPerformException {
        GenericTreeItem child;
        if (field.getNumber() == AgentConfig.AGENT_CLASS_ID_FIELD_NUMBER) {
            final LeafTreeItem leaf = new LeafTreeItem<>(field, getBuilder().getAgentClassId(), getBuilder(), editable);
            leaf.setEditingGraphicFactory(EditingGraphicFactory.getInstance(AgentClassIdEditingGraphic.class));
            leaf.setDescriptionGenerator((DescriptionGenerator<String>) value -> {
                try {
                    return LabelProcessor.getBestMatch(Registries.getClassRegistry().getAgentClassById(value).getLabel());
                } catch (CouldNotPerformException ex) {
                    return value;
                }
            });
            child = leaf;
        } else {
            child = super.createChild(field, editable);
        }
        return child;
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        Set<Integer> uneditableFields = super.getUneditableFields();
        if (!getBuilder().getAgentClassId().isEmpty()) {
            uneditableFields.add(AgentConfig.AGENT_CLASS_ID_FIELD_NUMBER);
        }
        return uneditableFields;
    }
}
