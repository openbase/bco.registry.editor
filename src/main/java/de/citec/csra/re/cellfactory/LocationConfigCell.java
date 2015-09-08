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
import de.citec.jps.core.JPService;
import de.citec.jps.preset.JPReadOnly;
import de.citec.jul.exception.CouldNotPerformException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
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

    private final ComboBox<String> locationConfigComboBox;

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
        locationConfigComboBox.setVisibleRowCount(5);
        locationConfigComboBox.setButtonCell(new LocationConfigCell.LocationConfigComboBoxCell());
        locationConfigComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {

            @Override
            public ListCell<String> call(ListView<String> p) {
                return new LocationConfigCell.LocationConfigComboBoxCell();
            }
        });
        locationConfigComboBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (locationConfigComboBox.getSelectionModel().getSelectedItem() != null) {
                    leaf.setValue(locationConfigComboBox.getSelectionModel().getSelectedItem());
                    setText(locationConfigComboBox.getSelectionModel().getSelectedItem());
                    commitEdit(leaf);
                }
            }
        });
    }

    @Override
    public void startEdit() {
        if (readOnly) {
            return;
        }
        super.startEdit();

        if (getItem() instanceof LeafContainer) {
            String descriptor = ((Leaf) getItem()).getDescriptor();
            if (descriptor.equals("parent_id") || descriptor.equals("child_id")) {
                try {
                    List<String> comboBoxList = new ArrayList();
                    for (LocationConfig locationConfig : locationRegistryRemote.getLocationConfigs()) {
                        comboBoxList.add(locationConfig.getId());
                    }
                    LocationConfig config = ((LocationConfig) ((LeafContainer) getItem()).getParent().getBuilder().build());
                    comboBoxList.remove(config.getId());
                    if (config.hasParentId()) {
                        comboBoxList.remove(config.getParentId());
                    }
                    for (String childId : config.getChildIdList()) {
                        comboBoxList.remove(childId);
                    }
                    Collections.sort(comboBoxList);
                    locationConfigComboBox.setItems(FXCollections.observableArrayList(comboBoxList));
                    super.setEditingGraphic(locationConfigComboBox);
                } catch (CouldNotPerformException ex) {
                    logger.warn("Could not receive data to fill the locationConfigComboBox", ex);
                }
            }
        }
    }

    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.

//        logger.info("get location read only property");
//        try {
//            readOnly = locationRegistryRemote.isLocationConfigRegistryReadOnly().get() || JPService.getProperty(JPReadOnly.class).getValue();
//            if (readOnly) {
//                setContextMenu(null);
//            }
//        } catch (CouldNotPerformException | InterruptedException | ExecutionException ex) {
//            readOnly = true;
//            logger.warn("Could not determine read only property for device classes", ex);
//        }
//        logger.info("got location read only property [" + readOnly + "]");
        if (readOnly) {
            setContextMenu(null);
        }
    }

    private class LocationConfigComboBoxCell extends ListCell<String> {

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                setText(item);
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
