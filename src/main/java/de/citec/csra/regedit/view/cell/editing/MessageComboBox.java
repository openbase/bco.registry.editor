/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.cell.editing;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import de.citec.csra.regedit.view.cell.ValueCell;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
import de.citec.csra.regedit.util.RemotePool;
import de.citec.jul.exception.InstantiationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class MessageComboBox extends ComboBox<String> {

    public MessageComboBox(ValueCell cell, Message.Builder parentBuilder, String fieldName) throws InstantiationException {
        super();
        setVisibleRowCount(5);
        setItems(sortedList(parentBuilder, fieldName));
        setValue((String) cell.getLeaf().getValue());
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

    private ObservableList<String> sortedList(Message.Builder parentBuilder, String fieldName) throws InstantiationException {
        try {
            Message type = getMessageEnumBoxType(fieldName);
            FieldDescriptor field = FieldDescriptorUtil.getField("id", type.toBuilder());
            List<String> list = new ArrayList<>();
            for (Message message : RemotePool.getInstance().getMessageList(type)) {
                list.add((String) message.getField(field));
            }
            if (parentBuilder instanceof LocationConfig.Builder) {
                list.remove(((LocationConfig.Builder) parentBuilder).getId());
                for (String childId : ((LocationConfig.Builder) parentBuilder).getChildIdList()) {
                    list.remove(childId);
                }
            }
            Collections.sort(list);
            return FXCollections.observableArrayList(list);
        } catch (Exception ex) {
            throw new InstantiationException(this, ex);
        }
    }

    public static GeneratedMessage getMessageEnumBoxType(String fieldName) {
        if (null != fieldName) {
            switch (fieldName) {
                case "location_id":
                case "child_id":
                case "parent_id":
                    return LocationConfig.getDefaultInstance();
                case "device_class_id":
                    return DeviceClass.getDefaultInstance();
            }
        }
        return null;
    }
}
