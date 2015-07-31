/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.csra.re.RegistryEditor;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.struct.Leaf;
import de.citec.csra.re.struct.Node;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jul.exception.CouldNotPerformException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author thuxohl
 */
public class DeviceConfigCell extends ValueCell {

    private final ComboBox<DeviceClass> deviceClassComboBox;
    private final ComboBox<LocationConfig> locationIdComboBox;
    private final ComboBox<LocationConfig> locationConfigComboBox;

    public DeviceConfigCell(DeviceRegistryRemote deviceRegistryRemote, LocationRegistryRemote locationRegistryRemote) {
        super(deviceRegistryRemote, locationRegistryRemote, null, null, null);

//        applyButton.setOnAction(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent event) {
//                Thread thread = new Thread(
//                        new Task<Boolean>() {
//                            @Override
//                            protected Boolean call() throws Exception {
//                                RegistryEditor.setModified(false);
//                                DeviceConfigContainer container = (DeviceConfigContainer) getItem();
//                                DeviceConfig deviceConfig = container.getBuilder().build();
//                                try {
//                                    if (deviceRegistryRemote.containsDeviceConfig(deviceConfig)) {
//                                        deviceRegistryRemote.updateDeviceConfig(deviceConfig);
//                                    } else {
//                                        deviceRegistryRemote.registerDeviceConfig(deviceConfig);
//                                        container.setNewNode(false);
//                                    }
//                                    container.setChanged(false);
//                                } catch (CouldNotPerformException ex) {
//                                    logger.warn("Could not register or update device config [" + deviceConfig + "]", ex);
//                                }
//                                return true;
//                            }
//                        });
//                thread.setDaemon(true);
//                thread.start();
//            }
//        });
//
//        cancel.setOnAction(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent event) {
//                Thread thread = new Thread(
//                        new Task<Boolean>() {
//                            @Override
//                            protected Boolean call() throws Exception {
//                                RegistryEditor.setModified(false);
//                                DeviceConfigContainer container = (DeviceConfigContainer) getItem();
//
//                                DeviceConfig deviceConfig = container.getBuilder().build();
//                                try {
//                                    if (container.getNewNode()) {
//                                        container.getParent().getChildren().remove(container);
//                                    } else {
//                                        int index = container.getParent().getChildren().indexOf(container);
//                                        container.getParent().getChildren().set(index, new DeviceConfigContainer(deviceRegistryRemote.getDeviceConfigById(deviceConfig.getId()).toBuilder()));
//                                    }
//                                } catch (CouldNotPerformException ex) {
//                                    logger.warn("Could cancel update of [" + deviceConfig + "]", ex);
//                                }
//                                return true;
//                            }
//                        });
//                thread.setDaemon(true);
//                thread.start();
//            }
//        });

        deviceClassComboBox = new ComboBox();
        deviceClassComboBox.setButtonCell(new DeviceClassComboBoxCell());
        deviceClassComboBox.setCellFactory(new Callback<ListView<DeviceClass>, ListCell<DeviceClass>>() {

            @Override
            public ListCell<DeviceClass> call(ListView<DeviceClass> param) {
                return new DeviceClassComboBoxCell();
            }
        });
        deviceClassComboBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (deviceClassComboBox.getSelectionModel().getSelectedItem() != null && !leaf.getValue().equals(deviceClassComboBox.getSelectionModel().getSelectedItem())) {
                    leaf.setValue(deviceClassComboBox.getSelectionModel().getSelectedItem());
                    setText(deviceClassComboBox.getSelectionModel().getSelectedItem().getId());
                    commitEdit(leaf);
                }
            }
        });

        locationIdComboBox = new ComboBox<>();
        locationIdComboBox.setButtonCell(new LocationConfigComboBoxCell());
        locationIdComboBox.setCellFactory(new Callback<ListView<LocationConfig>, ListCell<LocationConfig>>() {

            @Override
            public ListCell<LocationConfig> call(ListView<LocationConfig> param) {
                return new LocationConfigComboBoxCell();
            }
        });
        locationIdComboBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (locationIdComboBox.getSelectionModel().getSelectedItem() != null && !leaf.getValue().equals(locationIdComboBox.getSelectionModel().getSelectedItem())) {
                    leaf.setValue(locationIdComboBox.getSelectionModel().getSelectedItem().getId());
                    setText(locationIdComboBox.getSelectionModel().getSelectedItem().getId());
                    commitEdit(leaf);
                }
            }
        });

        locationConfigComboBox = new ComboBox<>();
        locationConfigComboBox.setButtonCell(new LocationConfigComboBoxCell());
        locationConfigComboBox.setCellFactory(new Callback<ListView<LocationConfig>, ListCell<LocationConfig>>() {

            @Override
            public ListCell<LocationConfig> call(ListView<LocationConfig> param) {
                return new LocationConfigComboBoxCell();
            }
        });
        locationConfigComboBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (locationConfigComboBox.getSelectionModel().getSelectedItem() != null && !leaf.getValue().equals(locationConfigComboBox.getSelectionModel().getSelectedItem())) {
                    leaf.setValue(locationConfigComboBox.getSelectionModel().getSelectedItem());
                    setText(locationConfigComboBox.getSelectionModel().getSelectedItem().getId());
                    commitEdit(leaf);
                }
            }
        });
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (getItem() instanceof Leaf) {
            if (((Leaf) getItem()).getValue() instanceof DeviceClass) {
                try {
                    deviceClassComboBox.setItems(FXCollections.observableArrayList(deviceRegistryRemote.getData().getDeviceClassList()));
                    super.setEditingGraphic(deviceClassComboBox);
                } catch (CouldNotPerformException ex) {
                    logger.warn("Could not receive data to fill the deviceClassComboBox", ex);
                }
            } else if (((Leaf) getItem()).getDescriptor().equals("location_id")) {
                try {
                    locationIdComboBox.setItems(FXCollections.observableArrayList(locationRegistryRemote.getData().getLocationConfigList()));
                    super.setEditingGraphic(locationIdComboBox);
                } catch (CouldNotPerformException ex) {
                    logger.warn("Could not receive data to fill the locationConfigComboBox", ex);
                }
            } else if (((Leaf) getItem()).getDescriptor().equals("location_config")) {
                try {
                    locationConfigComboBox.setItems(FXCollections.observableArrayList(locationRegistryRemote.getData().getLocationConfigList()));
                    super.setEditingGraphic(locationConfigComboBox);
                } catch (CouldNotPerformException ex) {
                    logger.warn("Could not receive data to fill the locationConfigComboBox", ex);
                }
            }
        }
    }

    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

//        if (item instanceof DeviceConfigContainer) {
//            DeviceConfigContainer container = (DeviceConfigContainer) item;
//            if (container.getNewNode() || container.hasChanged()) {
//                setGraphic(buttonBox);
//            } else {
//                setGraphic(null);
//            }
//
//            container.getChanged().addListener(new ChangeListener<Boolean>() {
//
//                @Override
//                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                    Platform.runLater(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            if (newValue) {
//                                setGraphic(buttonBox);
//                            } else {
//                                setGraphic(null);
//                            }
//                        }
//                    });
//                }
//            });
//        } else {
//            if (item != null && ((!"scope".equals(item.getDescriptor()) && (!"id".equals(item.getDescriptor()))))) {
//                setGraphic(null);
//            }
//        }

        if (item instanceof Leaf) {
            if (((Leaf) item).getValue() instanceof DeviceClass) {
                setText(((DeviceClass) ((Leaf) (item)).getValue()).getId());
            } else if (((Leaf) item).getDescriptor().equals("location_id")) {
                setText((String) ((Leaf) (item)).getValue());
            } else if (((Leaf) item).getDescriptor().equals("location_config")) {
                setText(((LocationConfig) ((Leaf) (item)).getValue()).getId());
            }
        }
    }

    private class DeviceClassComboBoxCell extends ListCell<DeviceClass> {

        @Override
        public void updateItem(DeviceClass item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                setText(item.getId());
            }
        }
    }

    public class LocationConfigComboBoxCell extends ListCell<LocationConfig> {

        @Override
        public void updateItem(LocationConfig item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                setText(item.getLabel());
            }
        }
    }
}
