package org.dc.bco.registry.editor.visual;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 DivineCooperation
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.protobuf.GeneratedMessage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableView;
import org.dc.bco.registry.editor.RegistryEditor;
import org.dc.bco.registry.editor.struct.GenericNodeContainer;
import org.dc.bco.registry.editor.struct.Node;
import org.dc.bco.registry.editor.util.RSTDefaultInstances;
import org.dc.bco.registry.editor.util.SendableType;
import org.dc.jul.exception.printer.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.device.DeviceClassType.DeviceClass;

/**
 *
 * @author thuxohl
 */
public class TreeTableViewContextMenu extends ContextMenu {

    private static final Logger logger = LoggerFactory.getLogger(TreeTableViewContextMenu.class);

    private final MenuItem addMenuItem;

    public TreeTableViewContextMenu(TreeTableView<Node> treeTableView, SendableType type) {
        addMenuItem = new MenuItem("Add");
        this.getItems().add(addMenuItem);
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
                        case CONNECTION_CONFIG:
                            newNode = new GenericNodeContainer("", RSTDefaultInstances.getDefaultConnectionConfig());
                            break;
                        default:
                            newNode = new GenericNodeContainer("", (GeneratedMessage.Builder) type.getDefaultInstanceForType().toBuilder());
                    }

                    newNode.setExpanded(true);
                    newNode.setChanged(true);
                    treeTableView.getRoot().getChildren().add(newNode);
                } catch (org.dc.jul.exception.InstantiationException ex) {
                    RegistryEditor.printException(ex, logger, LogLevel.ERROR);
                }
            }
        });
    }

}
