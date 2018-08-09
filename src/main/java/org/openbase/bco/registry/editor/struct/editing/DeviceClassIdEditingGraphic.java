package org.openbase.bco.registry.editor.struct.editing;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import rst.domotic.unit.device.DeviceClassType.DeviceClass;

import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DeviceClassIdEditingGraphic extends AbstractEditingGraphic<ComboBox<DeviceClass>, String> {

    public DeviceClassIdEditingGraphic(ValueType<String> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
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

    private ObservableList<DeviceClass> createSortedList() throws CouldNotPerformException {
        List<DeviceClass> deviceClasses = Registries.getClassRegistry().getDeviceClasses();
        deviceClasses.sort(Comparator.comparing(this::getDescription));
        return FXCollections.observableArrayList(deviceClasses);
    }


    @Override
    protected String getCurrentValue() {
        return getControl().getSelectionModel().getSelectedItem().getId();
    }

    @Override
    protected void init(final String value) {
        try {
            getControl().setItems(createSortedList());
            getControl().setValue(Registries.getClassRegistry().getDeviceClassById(value));
        } catch (CouldNotPerformException ex) {
            logger.error("Could create device class list", ex);
        }
    }

    private class MessageComboBoxCell extends ListCell<DeviceClass> {

        @Override
        public void updateItem(DeviceClass item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(getDescription(item));
            }
        }
    }

    protected String getDescription(final DeviceClass deviceClass) {
        try {
            return LabelProcessor.getBestMatch(deviceClass.getLabel());
        } catch (NotAvailableException ex) {
            return deviceClass.getId();
        }
    }
}
