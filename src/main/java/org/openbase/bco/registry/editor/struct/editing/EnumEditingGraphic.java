package org.openbase.bco.registry.editor.struct.editing;

import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class EnumEditingGraphic extends AbstractEditingGraphic<ComboBox<EnumValueDescriptor>, EnumValueDescriptor> {

    public EnumEditingGraphic(final ValueType<EnumValueDescriptor> valueType, final TreeTableCell<Object, Object> treeTableCell) {
        super(new ComboBox<>(), valueType, treeTableCell);
        getControl().setVisibleRowCount(10);
        getControl().setCellFactory(param -> new EnumValueDescriptorCell());
        getControl().setButtonCell(new EnumValueDescriptorCell());
        getControl().setOnAction((event) -> {
            commitEdit();
        });
    }

    @Override
    protected void commitEdit() {
        if (getControl().getSelectionModel().getSelectedItem() != null) {
            super.commitEdit();
        }
    }

    @Override
    protected EnumValueDescriptor getCurrentValue() {
        return getControl().getSelectionModel().getSelectedItem();
    }

    @Override
    protected void init(final EnumValueDescriptor value) {
        getControl().setItems(createSortedEnumList(value.getType()));
        getControl().setValue(value);
    }

    private ObservableList<EnumValueDescriptor> createSortedEnumList(EnumDescriptor enumDescriptor) {
        List<EnumValueDescriptor> values = new ArrayList<>(enumDescriptor.getValues());
        values.sort(Comparator.comparing(EnumValueDescriptor::getName));
        return FXCollections.observableArrayList(values);
    }

    private class EnumValueDescriptorCell extends ListCell<EnumValueDescriptor> {

        @Override
        public void updateItem(EnumValueDescriptor item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getName());
            }
        }
    }
}
