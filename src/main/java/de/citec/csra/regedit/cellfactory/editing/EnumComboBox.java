/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.cellfactory.editing;

import de.citec.csra.regedit.cellfactory.ValueCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class EnumComboBox extends ComboBox {

    public EnumComboBox(ValueCell cell, Class clazz) {
        super();
        setVisibleRowCount(5);
        setItems(removeUnkownType(clazz));
        setValue(cell.getLeaf().getValue());
        setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                if (getSelectionModel().getSelectedItem() != null && !cell.getLeaf().getValue().equals(getSelectionModel().getSelectedItem())) {
                    cell.getLeaf().setValue(getSelectionModel().getSelectedItem());
                    cell.commitEdit(cell.getLeaf());
                }
            }
        });
    }

    private ObservableList removeUnkownType(Class clazz) {
        ObservableList list = FXCollections.observableArrayList(clazz.getEnumConstants());
        try {
            list.remove(Enum.valueOf(clazz, "UNKNOWN"));
        } catch (Exception ex) {
        }
        return list;
    }
}
