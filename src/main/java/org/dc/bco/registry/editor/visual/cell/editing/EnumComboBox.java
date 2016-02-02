package org.dc.bco.registry.editor.visual.cell.editing;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 DivineCooperation
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.dc.bco.registry.editor.visual.cell.ValueCell;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class EnumComboBox extends ComboBox<EnumValueDescriptor> {

    public EnumComboBox(ValueCell cell, EnumValueDescriptor currentValue) {
        super();
        setVisibleRowCount(5);
        setItems(removeUnkownType(currentValue.getType()));
        setValue(currentValue);
        setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                if (getSelectionModel().getSelectedItem() != null && !cell.getLeaf().getValue().equals(getSelectionModel().getSelectedItem())) {
                    cell.getLeaf().setValue(getSelectionModel().getSelectedItem());
                    cell.commitEdit(cell.getLeaf());
                }
            }
        });
        setCellFactory(new Callback<ListView<EnumValueDescriptor>, ListCell<EnumValueDescriptor>>() {

            @Override
            public ListCell<EnumValueDescriptor> call(ListView<EnumValueDescriptor> p) {
                return new EnumComboBoxCell();
            }
        });
        setButtonCell(new EnumComboBoxCell());
    }

    private ObservableList removeUnkownType(EnumDescriptor enumDescriptor) {
        List<EnumValueDescriptor> values = new ArrayList<>(enumDescriptor.getValues());
        Collections.sort(values, new Comparator<EnumValueDescriptor>() {

            @Override
            public int compare(EnumValueDescriptor o1, EnumValueDescriptor o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                } else if (o1 == null) {
                    return 1;
                } else if (o2 == null) {
                    return -1;
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
//        for (int i = 0; i < values.size(); i++) {
//            if ("unknown".equalsIgnoreCase(values.get(i).getName())) {
//                values.remove(i);
//            }
//        }
        return FXCollections.observableArrayList(values);
    }

    private class EnumComboBoxCell extends ListCell<EnumValueDescriptor> {

        @Override
        public void updateItem(EnumValueDescriptor item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getName());
            }
        }
    }
}
