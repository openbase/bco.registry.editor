package org.openbase.bco.registry.editor.struct.editing;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
