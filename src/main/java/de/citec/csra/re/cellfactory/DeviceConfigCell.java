/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.csra.dm.remote.DeviceRegistryRemote;
import de.citec.csra.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.struct.leaf.Leaf;
import de.citec.csra.re.struct.node.DeviceConfigContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.NotAvailableException;
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
    private final ComboBox<LocationConfig> locationConfigComboBox;

    public DeviceConfigCell(DeviceRegistryRemote deviceRegistryRemote, LocationRegistryRemote locationRegistryRemote) {
        super(deviceRegistryRemote, locationRegistryRemote);

        applyButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Thread thread = new Thread(
                        new Task<Boolean>() {
                            @Override
                            protected Boolean call() throws Exception {
                                DeviceConfigContainer container = (DeviceConfigContainer) getItem();
                                DeviceConfig deviceConfig = container.getBuilder().build();
                                try {
                                    if (deviceRegistryRemote.containsDeviceConfig(deviceConfig)) {
                                        deviceRegistryRemote.updateDeviceConfig(deviceConfig);
                                    } else {
                                        deviceRegistryRemote.registerDeviceConfig(deviceConfig);
                                        container.setNewNode(false);
                                    }
                                    container.setChanged(false);
                                } catch (CouldNotPerformException ex) {
                                    logger.warn("Could not register or update device config [" + deviceConfig + "]", ex);
                                }
                                return true;
                            }
                        });
                thread.setDaemon(true);
                thread.start();
            }
        });

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
                    deviceClassComboBox.setItems(FXCollections.observableArrayList(deviceRegistryRemote.getData().getDeviceClassesList()));
                    setGraphic(deviceClassComboBox);
                } catch (NotAvailableException ex) {
                    logger.warn("Could not receive data to fill the deviceClassComboBox", ex);
                }
            } else if (((Leaf) getItem()).getValue() instanceof LocationConfig) {
                try {
                    locationConfigComboBox.setItems(FXCollections.observableArrayList(locationRegistryRemote.getData().getLocationConfigsList()));
                    setGraphic(locationConfigComboBox);
                } catch (NotAvailableException ex) {
                    logger.warn("Could not receive data to fill the locationConfigComboBox", ex);
                }
            }
        }
    }

    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (item instanceof DeviceConfigContainer) {
            setGraphic(applyButton);
            if (item.getDescriptor().equals("")) {
                applyButton.setVisible(true);
            }
            ((DeviceConfigContainer) item).getChanged().addListener(new ChangeListener<Boolean>() {

                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    applyButton.setVisible(newValue);
                }
            });
        } else if (item instanceof Leaf) {
            if (((Leaf) item).getValue() instanceof DeviceClass) {
                setText(((Leaf<DeviceClass>) item).getValue().getId());
            } else if (((Leaf) item).getValue() instanceof LocationConfig) {
                setText(((Leaf<LocationConfig>) item).getValue().getLabel());
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

    private class LocationConfigComboBoxCell extends ListCell<LocationConfig> {

        @Override
        public void updateItem(LocationConfig item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                setText(item.getId());
            }
        }
    }
}
