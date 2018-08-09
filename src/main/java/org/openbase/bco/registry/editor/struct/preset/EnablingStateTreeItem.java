package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.BuilderLeafTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.EnablingStateEditingGraphic;
import org.openbase.jul.exception.InitializationException;
import rst.domotic.state.EnablingStateType.EnablingState;
import rst.domotic.state.EnablingStateType.EnablingState.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class EnablingStateTreeItem extends BuilderLeafTreeItem<Builder> {

    public EnablingStateTreeItem(final FieldDescriptor fieldDescriptor, final EnablingState.Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
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
