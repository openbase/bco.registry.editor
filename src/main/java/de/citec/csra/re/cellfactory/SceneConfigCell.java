/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.csra.re.RegistryEditor;
import de.citec.csra.re.struct.leaf.Leaf;
import de.citec.csra.re.struct.node.Node;
import de.citec.csra.re.struct.node.SceneConfigContainer;
import de.citec.jps.core.JPService;
import de.citec.jps.preset.JPReadOnly;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.scm.remote.SceneRegistryRemote;
import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import rst.homeautomation.control.scene.SceneConfigType;
import rst.spatial.LocationConfigType;

/**
 *
 * @author thuxohl
 */
public class SceneConfigCell extends ValueCell {

    private final ComboBox<LocationConfigType.LocationConfig> locationIdComboBox;

    public SceneConfigCell(SceneRegistryRemote sceneRegistryRemote, LocationRegistryRemote locationRegistryRemote) {
        super(null, locationRegistryRemote, sceneRegistryRemote, null, null);

        applyButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Thread thread = new Thread(
                        new Task<Boolean>() {
                            @Override
                            protected Boolean call() throws Exception {
                                RegistryEditor.setModified(false);
                                SceneConfigContainer container = (SceneConfigContainer) getItem();

                                SceneConfigType.SceneConfig sceneConfig = container.getBuilder().build();
                                try {
                                    if (sceneRegistryRemote.containsSceneConfig(sceneConfig)) {
                                        sceneRegistryRemote.updateSceneConfig(sceneConfig);
                                    } else {
                                        sceneRegistryRemote.registerSceneConfig(sceneConfig);
                                        container.setNewNode(false);
                                    }
                                    container.setChanged(false);
                                } catch (CouldNotPerformException ex) {
                                    logger.warn("Could not register or update device class [" + sceneConfig + "]", ex);
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
                                SceneConfigContainer container = (SceneConfigContainer) getItem();

                                SceneConfigType.SceneConfig sceneConfig = container.getBuilder().build();
                                System.out.println("Scene build [" + sceneConfig + "]");
                                try {
                                    if (container.getNewNode()) {
                                        container.getParent().getChildren().remove(container);
                                    } else {
                                        int index = container.getParent().getChildren().indexOf(container);
                                        container.getParent().getChildren().set(index, new SceneConfigContainer(sceneRegistryRemote.getSceneConfigById(sceneConfig.getId()).toBuilder()));
                                    }
                                } catch (CouldNotPerformException ex) {
                                    logger.warn("Could cancel update of [" + sceneConfig + "]", ex);
                                }
                                return true;
                            }
                        });
                thread.setDaemon(true);
                thread.start();
            }
        });

        locationIdComboBox = new ComboBox<>();
        locationIdComboBox.setVisibleRowCount(5);
        locationIdComboBox.setButtonCell(new SceneConfigCell.LocationConfigComboBoxCell());
        locationIdComboBox.setCellFactory(new Callback<ListView<LocationConfigType.LocationConfig>, ListCell<LocationConfigType.LocationConfig>>() {

            @Override
            public ListCell<LocationConfigType.LocationConfig> call(ListView<LocationConfigType.LocationConfig> param) {
                return new SceneConfigCell.LocationConfigComboBoxCell();
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
    }

    @Override
    public void startEdit() {
        if (readOnly) {
            return;
        }
        super.startEdit();

        if (getItem() instanceof Leaf) {
            if (((Leaf) getItem()).getDescriptor().equals("location_id")) {
                try {
                    locationIdComboBox.setItems(FXCollections.observableArrayList(locationRegistryRemote.getData().getLocationConfigList()));
                    super.setEditingGraphic(locationIdComboBox);
                } catch (CouldNotPerformException ex) {
                    logger.warn("Could not receive data to fill the locationConfigComboBox", ex);
                }
            }
        }
    }

    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.

//        try {
//            readOnly = sceneRegistryRemote.isSceneConfigRegistryReadOnly().get() || JPService.getProperty(JPReadOnly.class).getValue();
//            if (readOnly) {
//                setContextMenu(null);
//            }
//        } catch (CouldNotPerformException | InterruptedException | ExecutionException ex) {
//            readOnly = true;
//            logger.warn("Could not determine read only property for device classes", ex);
//        }
        if (readOnly) {
            setContextMenu(null);
        }
    }

    public class LocationConfigComboBoxCell extends ListCell<LocationConfigType.LocationConfig> {

        @Override
        public void updateItem(LocationConfigType.LocationConfig item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                setText(item.getLabel());
            }
        }
    }
}
