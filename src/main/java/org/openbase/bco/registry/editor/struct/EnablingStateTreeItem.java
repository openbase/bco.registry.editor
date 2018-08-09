package org.openbase.bco.registry.editor.struct;

import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.editing.EnablingStateEditingGraphic;
import org.openbase.jul.exception.InitializationException;
import rst.domotic.state.EnablingStateType.EnablingState;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class EnablingStateTreeItem extends BuilderLeafTreeItem<EnablingState.Builder> {

    public EnablingStateTreeItem(final FieldDescriptor fieldDescriptor, final EnablingState.Builder builder) throws InitializationException {
        super(fieldDescriptor, builder);
    }

    @Override
    protected Node createValueGraphic() {
        return new Label(getBuilder().getValue().name());
    }

    @Override
    public Control getEditingGraphic(final TreeTableCell<ValueType, ValueType> cell) {
        return new EnablingStateEditingGraphic(getValue(), cell).getControl();
    }
}
