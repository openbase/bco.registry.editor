package org.openbase.bco.registry.editor.struct.editing;

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.EnumValueDescriptorCell;
import rst.domotic.state.EnablingStateType.EnablingState;
import rst.domotic.state.EnablingStateType.EnablingState.State;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class EnablingStateEditingGraphic extends AbstractEditingGraphic<ComboBox<EnumValueDescriptor>, EnablingState.Builder> {

    public EnablingStateEditingGraphic(final ValueType<EnablingState.Builder> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new ComboBox<>(), valueType, treeTableCell);
        getControl().setVisibleRowCount(10);
        getControl().setCellFactory(param -> new EnumValueDescriptorCell());
        getControl().setButtonCell(new EnumValueDescriptorCell());
        getControl().setOnAction((event) -> commitEdit());
    }

    @Override
    protected void commitEdit() {
        if (getControl().getSelectionModel().getSelectedItem() != null) {
            super.commitEdit();
        }
    }

    @Override
    protected EnablingState.Builder getCurrentValue() {
        State state = State.valueOf(getControl().getSelectionModel().getSelectedItem().getNumber());
        return getValueType().getValue().setValue(state);
    }

    @Override
    protected void init(final EnablingState.Builder value) {
        getControl().setItems(EnumEditingGraphic.createSortedEnumList(value.getValue().getDescriptorForType()));
        getControl().setValue(value.getValue().getValueDescriptor());
    }
}
