/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.csra.dm.remote.DeviceRegistryRemote;
import de.citec.csra.re.struct.node.DeviceClassContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.csra.re.struct.node.SendableNode;
import de.citec.csra.re.struct.node.UnitTypeListContainer;
import de.citec.csra.re.struct.node.VariableNode;
import de.citec.jul.exception.CouldNotPerformException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import org.slf4j.LoggerFactory;
import rst.homeautomation.device.DeviceClassType;
import rst.homeautomation.unit.UnitTypeHolderType;

/**
 * Cell factory to manage similar options for all cells in a row.
 * Initializes and manages the context menu for all child cells.
 * 
 * @author thuxohl
 */
public abstract class RowCell extends TreeTableCell<Node, Node> {

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    
    protected final DeviceRegistryRemote remote;
    
    private final ContextMenu contextMenu;
    private final MenuItem addMenuItem, removeMenuItem;
    
    public RowCell(DeviceRegistryRemote remote) {
        this.remote = remote;
        
        addMenuItem = new MenuItem("Add");
        removeMenuItem = new MenuItem("Remove");
        EventHandlerImpl eventHandler = new EventHandlerImpl();
        addMenuItem.setOnAction(eventHandler);
        removeMenuItem.setOnAction(eventHandler);
        contextMenu = new ContextMenu(addMenuItem, removeMenuItem);
    }

    @Override
    protected void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (item instanceof VariableNode) {
            setContextMenu(contextMenu);
        }
    }
    
    private class EventHandlerImpl implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            VariableNode variableNode = ((VariableNode) RowCell.this.getItem());
            if (event.getSource().equals(addMenuItem)) {
                VariableNode addedNode = null;
                if (variableNode instanceof DeviceClassContainer) {
                    addedNode = new DeviceClassContainer(DeviceClassType.DeviceClass.getDefaultInstance().toBuilder());
                    addedNode.setExpanded(true);
                    variableNode.getParent().setExpanded(true);
                    variableNode.getParent().getChildren().add(addedNode);
                } else if (variableNode instanceof UnitTypeListContainer ) {
                    UnitTypeListContainer listNode = ((UnitTypeListContainer) variableNode);
                    UnitTypeHolderType.UnitTypeHolder.Builder test = listNode.getBuilder().addUnitsBuilder();
                    listNode.add(test.getUnitType(), "units", listNode.getBuilder().getUnitsBuilderList().indexOf(test));
                    addedNode = listNode;
                }

//                if (addedNode instanceof SendableNode) {
//                    ((SendableNode) addedNode).setChanged(true);
//                } else {
//                    System.out.println(addedNode.getDescriptor());
//                    TreeItem<Node> parent = addedNode.getParent();
//                    while (!(parent instanceof SendableNode)) {
//                        parent = parent.getParent();
//                    }
//                    ((SendableNode) parent).setChanged(true);
//                }
            } else if (event.getSource().equals(removeMenuItem)) {
                variableNode.getParent().getChildren().remove(variableNode);

                if (variableNode instanceof DeviceClassContainer) {
                    try {
                        remote.removeDeviceClass((DeviceClassType.DeviceClass) variableNode.getBuilder().build());
                    } catch (CouldNotPerformException ex) {
                        logger.info("Could not remove deviceClass [" + variableNode.getBuilder().build() + "]", ex);
                    }
                }
            }
        }
    }
}
