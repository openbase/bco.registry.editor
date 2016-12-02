package org.openbase.bco.registry.editor.visual.cell.editing.combobox;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 openbase.org
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
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
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
import org.openbase.bco.registry.editor.util.RemotePool;
import org.openbase.bco.registry.editor.util.SendableType;
import org.openbase.bco.registry.editor.visual.cell.ValueCell;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.slf4j.LoggerFactory;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.agent.AgentClassType.AgentClass;
import rst.domotic.unit.app.AppClassType.AppClass;
import rst.domotic.unit.authorizationgroup.AuthorizationGroupConfigType.AuthorizationGroupConfig;
import rst.domotic.unit.connection.ConnectionConfigType.ConnectionConfig;
import rst.domotic.unit.device.DeviceClassType.DeviceClass;
import rst.domotic.unit.location.LocationConfigType.LocationConfig;
import rst.domotic.unit.unitgroup.UnitGroupConfigType.UnitGroupConfig;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class MessageComboBox extends ComboBox<Message> {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(MessageComboBox.class);

    private static final int DEFAULT_VISIBLE_ROW_COUNT = 5;
    private final MessageComboBoxConverter converter;

    public MessageComboBox(ValueCell cell, Message.Builder parentBuilder, String fieldName) throws InstantiationException {
        super();
        this.setVisibleRowCount(DEFAULT_VISIBLE_ROW_COUNT);
        this.converter = getConverterByMessageType(fieldName, parentBuilder);
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
                try {
                    if (getSelectionModel().getSelectedItem() != null && !cell.getLeaf().getValue().equals(getSelectionModel().getSelectedItem())) {
                        cell.getLeaf().setValue(converter.getValue(getSelectionModel().getSelectedItem()));
                        cell.setText(converter.getText(getSelectionModel().getSelectedItem()));
                        cell.commitEdit(cell.getLeaf());
                    }
                } catch (InterruptedException ex) {
                    ExceptionPrinter.printHistory(new CouldNotPerformException("Event handing skipped!", ex), logger, LogLevel.WARN);
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
            List<Message> list = RemotePool.getInstance().getMessageList(getMessageEnumBoxType(fieldName, parentBuilder));
            if (parentBuilder instanceof LocationConfig.Builder) {
                list.remove(RemotePool.getInstance().getById(ProtoBufFieldProcessor.getId(parentBuilder), parentBuilder));
                for (String childId : ((LocationConfig.Builder) parentBuilder).getChildIdList()) {
                    if (childId.equals(leafValue)) {
                        continue;
                    }
                    list.remove(RemotePool.getInstance().getById(childId, parentBuilder));
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
            if (parentBuilder instanceof AuthorizationGroupConfig.Builder) {
                for (String memberId : ((AuthorizationGroupConfig.Builder) parentBuilder).getMemberIdList()) {
                    if (memberId.equals(leafValue)) {
                        continue;
                    }
                    list.remove(RemotePool.getInstance().getById(memberId, UnitConfig.newBuilder()));
                }
            }
            if (parentBuilder instanceof UnitGroupConfig.Builder) {
                List<ServiceTemplate.ServiceType> serviceTypes = new ArrayList<>();
                for (ServiceTemplate serviceTemplate : ((UnitGroupConfig.Builder) parentBuilder).getServiceTemplateList()) {
                    serviceTypes.add(serviceTemplate.getType());
                }
                list.clear();
                list.addAll(RemotePool.getInstance().getDeviceRemote().getUnitConfigsByUnitTypeAndServiceTypes(((UnitGroupConfig.Builder) parentBuilder).getUnitType(), serviceTypes));
                for (String memberId : ((UnitGroupConfig.Builder) parentBuilder).getMemberIdList()) {
                    if (memberId.equals(leafValue)) {
                        continue;
                    }
                    list.remove(RemotePool.getInstance().getById(memberId, UnitConfig.newBuilder()));
                }
            }
            if (parentBuilder instanceof ConnectionConfig.Builder) {
                list.clear();
                for (String tileId : ((ConnectionConfig.Builder) parentBuilder).getTileIdList()) {
                    list.addAll(RemotePool.getInstance().getLocationRemote().getUnitConfigsByLocation(tileId));
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

    public static GeneratedMessage getMessageEnumBoxType(String fieldName, Message.Builder parentBuilder) {
        if (null != fieldName) {
            switch (fieldName) {
                case "location_id":
                case "child_id":
                case "parent_id":
                case "tile_id":
                    return SendableType.LOCATION_CONFIG.getDefaultInstanceForType();
                case "device_class_id":
                    return SendableType.DEVICE_CLASS.getDefaultInstanceForType();
                case "member_id":
                    if (parentBuilder instanceof AuthorizationGroupConfig.Builder) {
                        return SendableType.USER_CONFIG.getDefaultInstanceForType();
                    } else if (parentBuilder instanceof UnitGroupConfig.Builder) {
                        return SendableType.UNIT_CONFIG.getDefaultInstanceForType();
                    }
                case "owner_id":
                    return SendableType.USER_CONFIG.getDefaultInstanceForType();
                case "agent_class_id":
                    return SendableType.AGENT_CLASS.getDefaultInstanceForType();
                case "app_class_id":
                    return SendableType.APP_CLASS.getDefaultInstanceForType();
                case "unit_id":
                    if (parentBuilder instanceof ConnectionConfig.Builder) {
                        return SendableType.CONNECTION_CONFIG.getDefaultInstanceForType();
                    }
            }
        }
        return null;
    }

    public MessageComboBoxConverter getConverterByMessageType(String fieldName, Message.Builder parentBuilder) {
        GeneratedMessage msg = getMessageEnumBoxType(fieldName, parentBuilder);
        if (msg instanceof UnitConfig) {
            switch (((UnitConfig) msg).getType()) {
                case LOCATION:
                    return new LocationConfigComboBoxConverter();
                case USER:
                    return new UserConfigComboBoxConverter();
                case CONNECTION:
                    return new LocationConfigComboBoxConverter();
                default:
                    return new DefaultMessageComboBoxConverter();
            }
        } else if (msg instanceof AgentClass) {
            return new AgentClassComboBoxConverter();
        } else if (msg instanceof AppClass) {
            return new AppClassComboBoxConverter();
        } else if (msg instanceof DeviceClass) {
            return new DeviceClassComboBoxConverter();
        } else {
            return new DefaultMessageComboBoxConverter();
        }
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