/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.csra.re.RegistryEditor;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.struct.leaf.Leaf;
import de.citec.csra.re.struct.leaf.LeafContainer;
import de.citec.csra.re.struct.node.LocationConfigContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jul.exception.CouldNotPerformException;
import java.util.ArrayList;
import java.util.List;
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
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author thuxohl
 */
public class LocationConfigCell extends ValueCell {

    private final ComboBox<LocationConfig> locationConfigComboBox;
    private final ComboBox<UnitConfig> unitConfigComboBox;

    public LocationConfigCell(LocationRegistryRemote locationRegistryRemote) {
        super(null, locationRegistryRemote, null, null, null);

        applyButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Thread thread = new Thread(
                        new Task<Boolean>() {
                            @Override
                            protected Boolean call() throws Exception {
                                RegistryEditor.setModified(false);
                                LocationConfigContainer container = (LocationConfigContainer) getItem();
                                LocationConfig locationConfig = container.getBuilder().build();
                                try {
                                    if (locationRegistryRemote.containsLocationConfig(locationConfig)) {
                                        locationRegistryRemote.updateLocationConfig(locationConfig);
                                    } else {
                                        locationRegistryRemote.registerLocationConfig(locationConfig);
                                        container.setNewNode(false);
                                    }
                                    container.setChanged(false);
                                } catch (CouldNotPerformException ex) {
                                    logger.warn("Could not register or update location config [" + locationConfig + "]", ex);
                                }
                                return true;
                            }
                        });
                thread.setDaemon(true);
                thread.start();
            }
        });

        cancel.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Thread thread = new Thread(
                        new Task<Boolean>() {
                            @Override
                            protected Boolean call() throws Exception {
                                RegistryEditor.setModified(false);
                                LocationConfigContainer container = (LocationConfigContainer) getItem();

                                LocationConfig locationConfig = container.getBuilder().build();
                                try {
                                    if (container.getNewNode()) {
                                        container.getParent().getChildren().remove(container);
                                    } else {
                                        int index = container.getParent().getChildren().indexOf(container);
                                        container.getParent().getChildren().set(index, new LocationConfigContainer(locationRegistryRemote.getLocationConfigById(locationConfig.getId()).toBuilder()));
                                    }
                                } catch (CouldNotPerformException ex) {
                                    logger.warn("Could cancel update of [" + locationConfig + "]", ex);
                                }
                                return true;
                            }
                        });
                thread.setDaemon(true);
                thread.start();
            }
        });

        locationConfigComboBox = new ComboBox<>();
        locationConfigComboBox.setButtonCell(new LocationConfigCell.LocationConfigComboBoxCell());
        locationConfigComboBox.setCellFactory(new Callback<ListView<LocationConfig>, ListCell<LocationConfig>>() {

            @Override
            public ListCell<LocationConfig> call(ListView<LocationConfig> param) {
                return new LocationConfigCell.LocationConfigComboBoxCell();
            }
        });
        locationConfigComboBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (locationConfigComboBox.getSelectionModel().getSelectedItem() != null) {
                    leaf.setValue(locationConfigComboBox.getSelectionModel().getSelectedItem().getId());
                    setText(locationConfigComboBox.getSelectionModel().getSelectedItem().getId());
                    commitEdit(leaf);
                }
            }
        });

        unitConfigComboBox = new ComboBox<>();
        unitConfigComboBox.setButtonCell(new UnitConfigComboBoxCell());
        unitConfigComboBox.setCellFactory(new Callback<ListView<UnitConfig>, ListCell<UnitConfig>>() {

            @Override
            public ListCell<UnitConfig> call(ListView<UnitConfig> param) {
                return new LocationConfigCell.UnitConfigComboBoxCell();
            }
        });
        unitConfigComboBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (unitConfigComboBox.getSelectionModel().getSelectedItem() != null) {
                    leaf.setValue(unitConfigComboBox.getSelectionModel().getSelectedItem().getLabel());
                    setText(unitConfigComboBox.getSelectionModel().getSelectedItem().getLabel());
                    commitEdit(leaf);
                }
            }
        });
    }

    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (item instanceof LocationConfigContainer && ((LocationConfigContainer) item).getBuilder().getRoot()) {
            LocationConfigContainer container = (LocationConfigContainer) item;
            if (container.getNewNode() || container.hasChanged()) {
                setGraphic(buttonBox);
            } else {
                setGraphic(null);
            }
            container.getChanged().addListener(new ChangeListener<Boolean>() {

                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            if (newValue) {
                                setGraphic(buttonBox);
                            } else {
                                setGraphic(null);
                            }
                        }
                    });
                }
            });
        } else {
            if (item != null && ((!"scope".equals(item.getDescriptor()) && (!"id".equals(item.getDescriptor()))))) {
                setGraphic(null);
            }
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (getItem() instanceof LeafContainer) {
            if (((Leaf) getItem()).getDescriptor().equals("parent_id")) {
                try {
                    List<LocationConfig> comboBoxList = new ArrayList(locationRegistryRemote.getData().getLocationConfigList());
                    comboBoxList.remove((LocationConfig) ((LeafContainer) getItem()).getParent().getBuilder().build());
                    locationConfigComboBox.setItems(FXCollections.observableArrayList(comboBoxList));
                    super.setEditingGraphic(locationConfigComboBox);
                } catch (CouldNotPerformException ex) {
                    logger.warn("Could not receive data to fill the locationConfigComboBox", ex);
                }
            } else if (((Leaf) getItem()).getDescriptor().equals("unit_id")) {
                try {
                    unitConfigComboBox.setItems(FXCollections.observableArrayList(deviceRegistryRemote.getUnitConfigs()));
                    super.setEditingGraphic(unitConfigComboBox);
                } catch (CouldNotPerformException ex) {
                    logger.warn("Could not receive data to fill the unitConfigComboBox", ex);
                }
            }
        }
    }

    private class LocationConfigComboBoxCell extends ListCell<LocationConfig> {

        @Override
        public void updateItem(LocationConfig item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                setText(item.getLabel());
            }
        }
    }

    private class UnitConfigComboBoxCell extends ListCell<UnitConfig> {

        @Override
        public void updateItem(UnitConfig item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                setText(item.getLabel());
            }
        }
    }
}
