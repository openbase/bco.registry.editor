/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.apm.remote.AppRegistryRemote;
import de.citec.csra.re.RegistryEditor;
import de.citec.csra.re.struct.leaf.Leaf;
import de.citec.csra.re.struct.node.AppConfigContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.lm.remote.LocationRegistryRemote;
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
import rst.homeautomation.control.app.AppConfigType;
import rst.spatial.LocationConfigType;

/**
 *
 * @author thuxohl
 */
public class AppConfigCell extends ValueCell {

    private final ComboBox<LocationConfigType.LocationConfig> locationIdComboBox;
    
    public AppConfigCell(AppRegistryRemote appRegistryRemote, LocationRegistryRemote locationRegistryRemote) {
        super(null, locationRegistryRemote, null, null, appRegistryRemote);

        applyButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Thread thread = new Thread(
                        new Task<Boolean>() {
                            @Override
                            protected Boolean call() throws Exception {
                                RegistryEditor.setModified(false);
                                AppConfigContainer container = (AppConfigContainer) getItem();

                                AppConfigType.AppConfig appConfig = container.getBuilder().build();
                                try {
                                    if (appRegistryRemote.containsAppConfig(appConfig)) {
                                        appRegistryRemote.updateAppConfig(appConfig);
                                    } else {
                                        appRegistryRemote.registerAppConfig(appConfig);
                                        container.setNewNode(false);
                                    }
                                    container.setChanged(false);
                                } catch (CouldNotPerformException ex) {
                                    logger.warn("Could not register or update device class [" + appConfig + "]", ex);
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
                                AppConfigContainer container = (AppConfigContainer) getItem();

                                AppConfigType.AppConfig appConfig = container.getBuilder().build();
                                try {
                                    if (container.getNewNode()) {
                                        container.getParent().getChildren().remove(container);
                                    } else {
                                        int index = container.getParent().getChildren().indexOf(container);
                                        container.getParent().getChildren().set(index, new AppConfigContainer(appRegistryRemote.getAppConfigById(appConfig.getId()).toBuilder()));
                                    }
                                } catch (CouldNotPerformException ex) {
                                    logger.warn("Could cancel update of [" + appConfig + "]", ex);
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
        locationIdComboBox.setButtonCell(new AppConfigCell.LocationConfigComboBoxCell());
        locationIdComboBox.setCellFactory(new Callback<ListView<LocationConfigType.LocationConfig>, ListCell<LocationConfigType.LocationConfig>>() {

            @Override
            public ListCell<LocationConfigType.LocationConfig> call(ListView<LocationConfigType.LocationConfig> param) {
                return new AppConfigCell.LocationConfigComboBoxCell();
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
        super.updateItem(item, empty);

        if (item instanceof AppConfigContainer) {
            AppConfigContainer container = (AppConfigContainer) item;
            if ((container.getNewNode() || container.hasChanged()) && getGraphic() != buttonBox) {
                setGraphic(buttonBox);
            } else if (!(container.getNewNode() || container.hasChanged()) && getGraphic() != null) {
                setGraphic(null);
            }
            container.getChanged().addListener(new ChangeListener<Boolean>() {

                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            if (newValue && getGraphic() != buttonBox) {
                                setGraphic(buttonBox);
                            } else if (getGraphic() != null) {
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
