/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory.editing;

import com.google.protobuf.Message;
import de.citec.csra.re.cellfactory.ValueCell;
import de.citec.csra.re.util.FieldDescriptorUtil;
import de.citec.csra.re.util.RemotePool;
import de.citec.jul.exception.InstantiationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class MessageComboBox extends ComboBox {

    public MessageComboBox(ValueCell cell, Message msg, String fieldName) throws InstantiationException {
        super();
        setVisibleRowCount(5);
        setItems(sortedList(msg, fieldName));
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

    private ObservableList sortedList(Message msg, String fieldName) throws InstantiationException {
        try {
            List<Comparable> list = new ArrayList<>();
            for (Message message : RemotePool.getInstance().getMessageList(msg)) {
                list.add((Comparable) message.getField(FieldDescriptorUtil.getField(fieldName, msg.toBuilder())));
            }
            Collections.sort(list);
            return FXCollections.observableArrayList(list);
        } catch (Exception ex) {
            throw new InstantiationException(this, ex);
        }
    }
}
