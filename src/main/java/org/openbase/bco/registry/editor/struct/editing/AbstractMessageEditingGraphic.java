package org.openbase.bco.registry.editor.struct.editing;

import com.google.protobuf.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.jul.exception.CouldNotPerformException;

import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractMessageEditingGraphic<M extends Message> extends AbstractEditingGraphic<ComboBox<M>, String> {

    private DescriptionGenerator<M> descriptionGenerator;

    AbstractMessageEditingGraphic(ValueType<String> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new ComboBox<>(), valueType, treeTableCell);
        getControl().setVisibleRowCount(10);
        getControl().setCellFactory(param -> new MessageComboBoxCell());
        getControl().setButtonCell(new MessageComboBoxCell());
        getControl().setOnAction((event) -> commitEdit());
    }

    @Override
    protected void commitEdit() {
        if (getControl().getSelectionModel().getSelectedItem() != null) {
            super.commitEdit();
        }
    }

    private ObservableList<M> createSortedList() throws CouldNotPerformException {
        List<M> messages = getMessages();
        messages.sort(Comparator.comparing(m -> descriptionGenerator.getDescription(m)));
        return FXCollections.observableArrayList(messages);
    }


    @Override
    protected String getCurrentValue() {
        return getCurrentValue(getControl().getSelectionModel().getSelectedItem());
    }

    @Override
    protected void init(final String value) {
        try {
            descriptionGenerator = getDescriptionGenerator();
            getControl().setItems(createSortedList());
            if (!value.isEmpty()) {
                getControl().setValue(getMessage(value));
            }
        } catch (CouldNotPerformException ex) {
            logger.error("Could create message list", ex);
        }
    }

    private class MessageComboBoxCell extends ListCell<M> {

        @Override
        public void updateItem(M item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(descriptionGenerator.getDescription(item));
            }
        }
    }

    protected abstract List<M> getMessages() throws CouldNotPerformException;

    protected abstract String getCurrentValue(final M message);

    protected abstract M getMessage(final String value) throws CouldNotPerformException;

    protected abstract DescriptionGenerator<M> getDescriptionGenerator();
}
