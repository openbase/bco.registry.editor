/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.agm.remote.AgentRegistryRemote;
import de.citec.csra.re.RegistryEditor;
import de.citec.csra.re.struct.leaf.Leaf;
import de.citec.csra.re.struct.node.AgentConfigContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.jps.core.JPService;
import de.citec.jps.preset.JPReadOnly;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.lm.remote.LocationRegistryRemote;
import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import rst.homeautomation.control.agent.AgentConfigType;
import rst.spatial.LocationConfigType;

/**
 *
 * @author thuxohl
 */
public class AgentConfigCell extends ValueCell {

    private final ComboBox<LocationConfigType.LocationConfig> locationIdComboBox;

    public AgentConfigCell(AgentRegistryRemote agentRegistryRemote, LocationRegistryRemote locationRegistryRemote) {
        super(null, locationRegistryRemote, null, agentRegistryRemote, null);

        applyButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Thread thread = new Thread(
                        new Task<Boolean>() {
                            @Override
                            protected Boolean call() throws Exception {
                                RegistryEditor.setModified(false);
                                AgentConfigContainer container = (AgentConfigContainer) getItem();

                                AgentConfigType.AgentConfig agentConfig = container.getBuilder().build();
                                try {
                                    if (agentRegistryRemote.containsAgentConfig(agentConfig)) {
                                        agentRegistryRemote.updateAgentConfig(agentConfig);
                                    } else {
                                        agentRegistryRemote.registerAgentConfig(agentConfig);
                                        container.setNewNode(false);
                                    }
                                    container.setChanged(false);
                                } catch (CouldNotPerformException ex) {
                                    logger.warn("Could not register or update device class [" + agentConfig + "]", ex);
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
                                AgentConfigContainer container = (AgentConfigContainer) getItem();

                                AgentConfigType.AgentConfig agentConfig = container.getBuilder().build();
                                try {
                                    if (container.getNewNode()) {
                                        container.getParent().getChildren().remove(container);
                                    } else {
                                        int index = container.getParent().getChildren().indexOf(container);
                                        container.getParent().getChildren().set(index, new AgentConfigContainer(agentRegistryRemote.getAgentConfigById(agentConfig.getId()).toBuilder()));
                                    }
                                } catch (CouldNotPerformException ex) {
                                    logger.warn("Could cancel update of [" + agentConfig + "]", ex);
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
        locationIdComboBox.setButtonCell(new AgentConfigCell.LocationConfigComboBoxCell());
        locationIdComboBox.setCellFactory(new Callback<ListView<LocationConfigType.LocationConfig>, ListCell<LocationConfigType.LocationConfig>>() {

            @Override
            public ListCell<LocationConfigType.LocationConfig> call(ListView<LocationConfigType.LocationConfig> param) {
                return new AgentConfigCell.LocationConfigComboBoxCell();
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
//            readOnly = agentRegistryRemote.isAgentConfigRegistryReadOnly().get() || JPService.getProperty(JPReadOnly.class).getValue();
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
