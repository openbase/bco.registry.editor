/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.cell.editing;

import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import de.citec.csra.regedit.view.cell.ValueCell;
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
        for (int i = 0; i < values.size(); i++) {
            if ("unknown".equalsIgnoreCase(values.get(i).getName())) {
                values.remove(i);
            }
        }
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
