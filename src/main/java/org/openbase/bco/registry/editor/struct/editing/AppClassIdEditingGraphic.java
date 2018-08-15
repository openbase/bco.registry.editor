package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import rst.domotic.unit.app.AppClassType.AppClass;

import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class AppClassIdEditingGraphic extends AbstractMessageEditingGraphic<AppClass> {

    public AppClassIdEditingGraphic(ValueType<String> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(valueType, treeTableCell);
    }

    @Override
    protected List<AppClass> getMessages() throws CouldNotPerformException {
        return Registries.getClassRegistry().getAppClasses();
    }

    @Override
    protected String getCurrentValue(final AppClass message) {
        return message.getId();
    }

    @Override
    protected AppClass getMessage(final String value) throws CouldNotPerformException {
        return Registries.getClassRegistry().getAppClassById(value);
    }

    @Override
    protected DescriptionGenerator<AppClass> getDescriptionGenerator() {
        return value -> {
            try {
                return LabelProcessor.getBestMatch(value.getLabel());
            } catch (NotAvailableException e) {
                return value.getId();
            }
        };
    }
}
