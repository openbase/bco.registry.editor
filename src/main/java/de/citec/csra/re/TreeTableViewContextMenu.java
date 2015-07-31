/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re;

import com.google.protobuf.GeneratedMessage;
import de.citec.csra.re.struct.GenericNodeContainer;
import de.citec.csra.re.struct.Node;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableView;
import rst.homeautomation.control.agent.AgentRegistryType;
import rst.homeautomation.control.app.AppRegistryType;
import rst.homeautomation.control.scene.SceneRegistryType;
import rst.homeautomation.device.DeviceRegistryType;
import rst.spatial.LocationRegistryType;

/**
 *
 * @author thuxohl
 */
public class TreeTableViewContextMenu extends ContextMenu {

    private final MenuItem addMenuItem;
    private final SendableType type;

    public TreeTableViewContextMenu(TreeTableView<Node> treeTableView, SendableType type) {
        addMenuItem = new MenuItem("Add");
        this.type = type;
        addMenuItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    GenericNodeContainer newNode = null;
                    switch (type) {
                        case DEVICE_CLASS:
                            newNode = new GenericNodeContainer(DeviceRegistryType.DeviceRegistry.DEVICE_CLASS_FIELD_NUMBER, null);
                            break;
                        case DEVICE_CONFIG:
                            newNode = new GenericNodeContainer(DeviceRegistryType.DeviceRegistry.DEVICE_CONFIG_FIELD_NUMBER, null);
                            break;
                        case LOCATION_CONFIG:
                            newNode = new GenericNodeContainer(LocationRegistryType.LocationRegistry.LOCATION_CONFIG_FIELD_NUMBER, null);
                            break;
                        case SCENE_CONFIG:
                            newNode = new GenericNodeContainer(SceneRegistryType.SceneRegistry.SCENE_CONFIG_FIELD_NUMBER, null);
                            break;
                        case AGENT_CONFIG:
                            newNode = new GenericNodeContainer(AgentRegistryType.AgentRegistry.AGENT_CONFIG_FIELD_NUMBER, null);
                            break;
                        case APP_CONFIG:
                            newNode = new GenericNodeContainer(AppRegistryType.AppRegistry.APP_CONFIG_FIELD_NUMBER, null);
                            break;
                    }

                    if (newNode != null) {
                        newNode.setExpanded(true);
//                    newNode.setChanged(true);
//                    newNode.setNewNode(true);
                        treeTableView.getRoot().getChildren().add(newNode);
                    }
                } catch (de.citec.jul.exception.InstantiationException ex) {
                    //TODO
                }
            }
        });
        this.getItems().add(addMenuItem);
    }

    public enum SendableType {

        AGENT_CONFIG,
        APP_CONFIG,
        DEVICE_CLASS,
        DEVICE_CONFIG,
        LOCATION_CONFIG,
        SCENE_CONFIG;
    }
}
