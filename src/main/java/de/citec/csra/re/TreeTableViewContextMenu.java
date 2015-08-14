/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re;

import de.citec.csra.re.struct.GenericNodeContainer;
import de.citec.csra.re.struct.Node;
import de.citec.csra.re.util.RSTDefaultInstances;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableView;
import rst.homeautomation.device.DeviceClassType.DeviceClass;

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
                            newNode = new GenericNodeContainer("", DeviceClass.getDefaultInstance().toBuilder());
                            break;
                        case DEVICE_CONFIG:
                            newNode = new GenericNodeContainer("", RSTDefaultInstances.getDefaultDeviceConfig());
                            break;
                        case LOCATION_CONFIG:
                            newNode = new GenericNodeContainer("", RSTDefaultInstances.getDefaultLocationConfig());
                            break;
                        case SCENE_CONFIG:
                            newNode = new GenericNodeContainer("", RSTDefaultInstances.getDefaultSceneConfig());
                            break;
                        case AGENT_CONFIG:
                            newNode = new GenericNodeContainer("", RSTDefaultInstances.getDefaultAgentConfig());
                            break;
                        case APP_CONFIG:
                            newNode = new GenericNodeContainer("", RSTDefaultInstances.getDefaultAppConfig());
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
