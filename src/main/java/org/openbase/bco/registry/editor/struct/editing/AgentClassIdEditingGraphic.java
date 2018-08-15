package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import rst.domotic.unit.agent.AgentClassType.AgentClass;

import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class AgentClassIdEditingGraphic extends AbstractMessageEditingGraphic<AgentClass> {

    public AgentClassIdEditingGraphic(ValueType<String> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(valueType, treeTableCell);
    }

    @Override
    protected List<AgentClass> getMessages() throws CouldNotPerformException {
        return Registries.getClassRegistry().getAgentClasses();
    }

    @Override
    protected String getCurrentValue(final AgentClass message) {
        return message.getId();
    }

    @Override
    protected AgentClass getMessage(final String value) throws CouldNotPerformException {
        return Registries.getClassRegistry().getAgentClassById(value);
    }

    @Override
    protected DescriptionGenerator<AgentClass> getDescriptionGenerator() {
        return value -> {
            try {
                return LabelProcessor.getBestMatch(value.getLabel());
            } catch (NotAvailableException e) {
                return value.getId();
            }
        };
    }
}
