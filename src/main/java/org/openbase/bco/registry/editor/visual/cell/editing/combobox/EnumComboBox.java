package org.openbase.bco.registry.editor.visual.cell.editing.combobox;

/*
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

import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import org.openbase.bco.registry.editor.visual.cell.ValueCell;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class EnumComboBox extends ComboBox<EnumValueDescriptor> {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(EnumComboBox.class);

    public EnumComboBox(ValueCell cell, EnumValueDescriptor currentValue) {
        super();
        setVisibleRowCount(5);
        setItems(sortEnumValues(currentValue.getType()));
        setValue(currentValue);
        setOnAction(event -> {
            try {
                if (getSelectionModel().getSelectedItem() != null && !cell.getLeaf().getValue().equals(getSelectionModel().getSelectedItem())) {
                    cell.getLeaf().setValue(getSelectionModel().getSelectedItem());
                    cell.setGraphic(new Label(((EnumValueDescriptor) cell.getLeaf().getValue()).getName()));
                    cell.commitEdit(cell.getLeaf());
                }
            } catch (InterruptedException ex) {
                ExceptionPrinter.printHistory(new CouldNotPerformException("Event handing skipped!", ex), logger, LogLevel.WARN);
            }
        });
        setCellFactory(p -> new EnumValueDescriptorCell());
        setButtonCell(new EnumValueDescriptorCell());
    }

    private ObservableList<EnumValueDescriptor> sortEnumValues(EnumDescriptor enumDescriptor) {
        List<EnumValueDescriptor> values = new ArrayList<>(enumDescriptor.getValues());
        values.sort((o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            }
            return o1.getName().compareTo(o2.getName());
        });
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
