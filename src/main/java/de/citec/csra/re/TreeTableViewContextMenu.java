/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re;

import com.google.protobuf.GeneratedMessage;
import de.citec.csra.re.struct.node.DeviceClassContainer;
import de.citec.csra.re.struct.node.DeviceConfigContainer;
import de.citec.csra.re.struct.node.LocationConfigContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.csra.re.struct.node.SendableNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableView;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author thuxohl
 * @param <M>
 */
public class TreeTableViewContextMenu<M extends GeneratedMessage> extends ContextMenu {

    private final MenuItem addMenuItem;

    public TreeTableViewContextMenu(TreeTableView<Node> treeTableView, M type) {
        addMenuItem = new MenuItem("Add");
        addMenuItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                SendableNode newNode = null;
                if (type instanceof DeviceClass) {
                    newNode = new DeviceClassContainer(DeviceClass.newBuilder());
                } else if (type instanceof DeviceConfig) {
                    newNode = new DeviceConfigContainer(DeviceConfig.newBuilder());
                }

                if (newNode != null) {
                    newNode.setExpanded(true);
                    newNode.setChanged(true);
                    newNode.setNewNode(true);
                    treeTableView.getRoot().getChildren().add(newNode);
                }

                if (type instanceof LocationConfig && treeTableView.getRoot() == null ) {
                    newNode = new LocationConfigContainer(LocationConfig.newBuilder());
                    newNode.setExpanded(true);
                    newNode.setChanged(true);
                    newNode.setNewNode(true);
                    treeTableView.setRoot(newNode);
                    addMenuItem.setVisible(false);
                }
            }
        });
        this.getItems().add(addMenuItem);
    }

}
