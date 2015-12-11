/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.cell.editing;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
import de.citec.csra.regedit.view.cell.ValueCell;
import de.citec.csra.regedit.util.RemotePool;
import de.citec.jul.exception.InstantiationException;
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
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class MessageComboBox extends ComboBox<Message> {

    private static final int DEFAULT_VISIBLE_ROW_COUNT = 5;
    private final MessageComboBoxConverterInterface converter;

    public MessageComboBox(ValueCell cell, Message.Builder parentBuilder, String fieldName) throws InstantiationException {
        super();
        this.setVisibleRowCount(DEFAULT_VISIBLE_ROW_COUNT);
        this.converter = getConverterByMessageType(fieldName);
        this.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {

            @Override
            public ListCell<Message> call(ListView<Message> param) {
                return new MessageComboBoxCell();
            }
        });
        this.setButtonCell(new MessageComboBoxCell());
        this.setItems(sortedList(parentBuilder, fieldName, cell.getLeaf().getValue()));
        this.setStartingValue(cell.getLeaf().getValue());
        this.setOnAction(new EventHandler() {

            @Override
            public void handle(Event event) {
                if (getSelectionModel().getSelectedItem() != null && !cell.getLeaf().getValue().equals(getSelectionModel().getSelectedItem())) {
                    cell.getLeaf().setValue(converter.getValue(getSelectionModel().getSelectedItem()));
                    cell.commitEdit(cell.getLeaf());
                }
            }
        });
    }

    private void setStartingValue(Object value) {
        for (Message msg : this.getItems()) {
            if (converter.getValue(msg).equals(value)) {
                this.setValue(msg);
                return;
            }
        }
    }

    private ObservableList<Message> sortedList(Message.Builder parentBuilder, String fieldName, Object leafValue) throws InstantiationException {
        try {
            List<Message> list = RemotePool.getInstance().getMessageList(getMessageEnumBoxType(fieldName));
            if (parentBuilder instanceof LocationConfig.Builder) {
                list.remove(RemotePool.getInstance().getById(FieldDescriptorUtil.getId(parentBuilder), parentBuilder).build());
                for (String childId : ((LocationConfig.Builder) parentBuilder).getChildIdList()) {
                    if (childId.equals(leafValue)) {
                        continue;
                    }
                    list.remove(RemotePool.getInstance().getById(childId, parentBuilder).build());
                }
            }
            if ("tile_id".equals(fieldName)) {
                for (int i = 0; i < list.size(); i++) {
                    LocationConfig location = (LocationConfig) list.get(i);
                    if (location.getType() != LocationConfig.LocationType.TILE) {
                        list.remove(i);
                        i--;
                    }
                }
            }
            Collections.sort(list, new Comparator<Message>() {

                @Override
                public int compare(Message o1, Message o2) {
                    if (o1 == null && o2 == null) {
                        return 0;
                    } else if (o1 == null) {
                        return 1;
                    } else if (o2 == null) {
                        return -1;
                    } else {
                        return converter.getText(o1).compareTo(converter.getText(o2));
                    }
                }
            });
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
                case "tile_id":
                    return LocationConfig.getDefaultInstance();
                case "device_class_id":
                    return DeviceClass.getDefaultInstance();
            }
        }
        return null;
    }

    public MessageComboBoxConverterInterface getConverterByMessageType(String fieldName) {
        GeneratedMessage msg = getMessageEnumBoxType(fieldName);
        if (msg instanceof LocationConfig) {
            return new LocationConfigComboBoxConverter();
        } else if (msg instanceof DeviceClass) {
            return new DefaultMessageComboBoxConverter();
        }
        return null;
    }

    private class MessageComboBoxCell extends ListCell<Message> {

        @Override
        public void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(converter.getText(item));
            }
        }
    }
}
