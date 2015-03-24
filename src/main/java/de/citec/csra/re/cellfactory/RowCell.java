/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import com.google.protobuf.Descriptors;
import de.citec.csra.dm.remote.DeviceRegistryRemote;
import de.citec.csra.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.struct.leaf.Leaf;
import de.citec.csra.re.struct.leaf.LeafContainer;
import de.citec.csra.re.struct.node.DeviceClassContainer;
import de.citec.csra.re.struct.node.DeviceConfigContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.csra.re.struct.node.NodeContainer;
import de.citec.csra.re.struct.node.ServiceConfigContainer;
import de.citec.csra.re.struct.node.ServiceConfigListContainer;
import de.citec.csra.re.struct.node.UnitConfigContainer;
import de.citec.csra.re.struct.node.UnitConfigListContainer;
import de.citec.csra.re.struct.node.UnitTypeListContainer;
import de.citec.csra.re.struct.node.VariableNode;
import de.citec.jul.exception.CouldNotPerformException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableCell;
import org.slf4j.LoggerFactory;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.service.ServiceConfigType.ServiceConfig;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.homeautomation.unit.UnitTypeHolderType.UnitTypeHolder;

/**
 * Cell factory to manage similar options for all cells in a row. Initializes
 * and manages the context menu for all child cells.
 *
 * @author thuxohl
 */
public abstract class RowCell extends TreeTableCell<Node, Node> {

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    protected final DeviceRegistryRemote deviceRegistryRemote;
    protected final LocationRegistryRemote locationRegistryRemote;

    private final ContextMenu contextMenu;
    private final MenuItem addMenuItem, removeMenuItem;

    public RowCell(DeviceRegistryRemote deviceRegistryRemote, LocationRegistryRemote locationRegistryRemote) {
        this.deviceRegistryRemote = deviceRegistryRemote;
        this.locationRegistryRemote = locationRegistryRemote;

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

        if ((item instanceof UnitTypeListContainer) || (item instanceof UnitConfigListContainer) || (item instanceof ServiceConfigListContainer)) {
            addMenuItem.setVisible(true);
            removeMenuItem.setVisible(false);
            setContextMenu(contextMenu);
        } else if (item instanceof VariableNode) {
            addMenuItem.setVisible(true);
            removeMenuItem.setVisible(true);
            setContextMenu(contextMenu);
        } else if (item instanceof Leaf) {
            if (((Leaf) item).getValue() instanceof UnitTypeHolder.UnitType) {
                addMenuItem.setVisible(true);
                removeMenuItem.setVisible(true);
                setContextMenu(contextMenu);
            }
        } else {
            setContextMenu(null);
        }
    }

    private class EventHandlerImpl implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (event.getSource().equals(addMenuItem)) {
                addAction(RowCell.this.getItem());
            } else if (event.getSource().equals(removeMenuItem)) {
                removeAction(RowCell.this.getItem());
            }
        }

        private void addAction(Node add) {
            if (add instanceof DeviceClassContainer) {
                NodeContainer parent = (NodeContainer) ((DeviceClassContainer) add).getParent().getValue();
                DeviceClassContainer addedNode = new DeviceClassContainer(DeviceClass.getDefaultInstance().toBuilder());
                addedNode.setChanged(true);
                addedNode.setNewNode(true);
                addedNode.setExpanded(true);
                parent.setExpanded(true);
                parent.getChildren().add(addedNode);
            } else if (add instanceof DeviceConfigContainer) {
                NodeContainer parent = (NodeContainer) ((DeviceConfigContainer) add).getParent().getValue();
                DeviceConfigContainer addedNode = new DeviceConfigContainer(DeviceConfig.getDefaultInstance().toBuilder());
                addedNode.setChanged(true);
                addedNode.setNewNode(true);
                addedNode.setExpanded(true);
                parent.setExpanded(true);
                parent.getChildren().add(addedNode);
            } else if (add instanceof UnitTypeListContainer) {
                UnitTypeListContainer listNode = ((UnitTypeListContainer) add);
                UnitTypeHolder.Builder unitTypeBuilder = listNode.getBuilder().addUnitsBuilder();
                listNode.add(unitTypeBuilder.getUnitType(), "units", listNode.getBuilder().getUnitsBuilderList().indexOf(unitTypeBuilder));
                listNode.setExpanded(true);
                listNode.setSendableChanged();
            } else if (add instanceof Leaf) {
                if (((Leaf) add).getValue() instanceof UnitTypeHolder.UnitType) {
                    UnitTypeListContainer listNode = ((UnitTypeListContainer) ((LeafContainer) add).getParent());
                    UnitTypeHolder.Builder unitTypeBuilder = listNode.getBuilder().addUnitsBuilder();
                    listNode.add(unitTypeBuilder.getUnitType(), "units", listNode.getBuilder().getUnitsBuilderList().indexOf(unitTypeBuilder));
                    listNode.setExpanded(true);
                    listNode.setSendableChanged();
                }
            } else if (add instanceof UnitConfigListContainer) {
                UnitConfigListContainer listNode = ((UnitConfigListContainer) add);
                UnitConfig.Builder unitConfigBuilder = listNode.getBuilder().addUnitConfigsBuilder();
                listNode.add(new UnitConfigContainer(unitConfigBuilder));
                listNode.setExpanded(true);
                listNode.setSendableChanged();
            } else if (add instanceof UnitConfigContainer) {
                UnitConfigListContainer listNode = ((UnitConfigListContainer) ((UnitConfigContainer) add).getParent());
                UnitConfig.Builder unitConfigBuilder = listNode.getBuilder().addUnitConfigsBuilder();
                listNode.add(new UnitConfigContainer(unitConfigBuilder));
                listNode.setExpanded(true);
                listNode.setSendableChanged();
            } else if (add instanceof ServiceConfigListContainer) {
                ServiceConfigListContainer listNode = ((ServiceConfigListContainer) add);
                ServiceConfig.Builder serviceConfigBuilder = listNode.getBuilder().addServiceConfigsBuilder();
                listNode.add(new ServiceConfigContainer(serviceConfigBuilder));
                listNode.setExpanded(true);
                listNode.setSendableChanged();
            } else if (add instanceof ServiceConfigContainer) {
                ServiceConfigListContainer listNode = ((ServiceConfigListContainer) ((ServiceConfigContainer) add).getParent());
                ServiceConfig.Builder serviceConfigBuilder = listNode.getBuilder().addServiceConfigsBuilder();
                listNode.add(new ServiceConfigContainer(serviceConfigBuilder));
                listNode.setExpanded(true);
                listNode.setSendableChanged();
            }
        }

        private void removeAction(Node remove) {
            // check if the removed item is an instance of classes that have to be directly
            // removed in the registry [deviceClass, deviceConfig]
            if (remove instanceof DeviceClassContainer) {
                DeviceClassContainer item = (DeviceClassContainer) remove;
                if (item.getNewNode()) {
                    item.getParent().getChildren().remove(item);
                } else {
                    DeviceClass deviceClass = item.getBuilder().build();
                    try {
                        if (deviceRegistryRemote.containsDeviceClass(deviceClass)) {
                            deviceRegistryRemote.removeDeviceClass(deviceClass);
                        }
                        item.getParent().getChildren().remove(item);
                    } catch (CouldNotPerformException ex) {
                        logger.info("Could not remove deviceClass [" + item.getBuilder().build() + "]", ex);
                    }
                }
            } else if (remove instanceof DeviceConfigContainer) {
                DeviceConfigContainer item = (DeviceConfigContainer) remove;
                if (item.getNewNode()) {
                    item.getParent().getChildren().remove(item);
                } else {
                    DeviceConfig deviceConfig = item.getBuilder().build();
                    try {
                        if (deviceRegistryRemote.containsDeviceConfig(deviceConfig)) {
                            deviceRegistryRemote.removeDeviceConfig(deviceConfig);
                        }
                        item.getParent().getChildren().remove(item);
                    } catch (CouldNotPerformException ex) {
                        logger.info("Could not remove deviceConfig [" + item.getBuilder().build() + "]", ex);
                    }
                }
                // check for repeated fields: unitTypes[deviceClasses], unitConfigs[deviceConfigs], serviceConfigs[unitConfigs]
            } else if (remove instanceof Leaf) {
                LeafContainer item = (LeafContainer) remove;
                if (item.getValue() instanceof UnitTypeHolder.UnitType) {
                    Descriptors.FieldDescriptor field = item.getParent().getBuilder().getDescriptorForType().findFieldByName("units");

                    // protobuf does not provide a good api to delete items from a repeated field
                    //  therefore the field has to be cleared entirely and then set with a  new list with the item removed 
                    List<UnitTypeHolder> unitTypeList = new ArrayList<>((List<UnitTypeHolder>) item.getParent().getBuilder().getField(field));
                    unitTypeList.remove(item.getIndex());
                    item.getParent().getBuilder().clearField(field);
                    item.getParent().getBuilder().setField(field, unitTypeList);
                }

                item.getParent().setSendableChanged();
                item.getParent().getChildren().remove(item);
            } else if (remove instanceof UnitConfigContainer) {
                UnitConfigContainer item = (UnitConfigContainer) remove;
                NodeContainer parent = (NodeContainer) item.getParent().getValue();
                Descriptors.FieldDescriptor field = parent.getBuilder().getDescriptorForType().findFieldByName("unit_configs");

                List<UnitConfig> unitConfigList = new ArrayList<>((List<UnitConfig>) parent.getBuilder().getField(field));
                unitConfigList.remove(item.getParent().getChildren().indexOf(item));

                parent.getBuilder().clearField(field);
                parent.getBuilder().setField(field, unitConfigList);

                item.setSendableChanged();
                item.getParent().getChildren().remove(item);
            } else if (remove instanceof ServiceConfigContainer) {
                ServiceConfigContainer item = (ServiceConfigContainer) remove;
                NodeContainer parent = (NodeContainer) item.getParent().getValue();

                Descriptors.FieldDescriptor field = parent.getBuilder().getDescriptorForType().findFieldByName("service_configs");

                List<ServiceConfig> serviceConfigList = new ArrayList<>((List<ServiceConfig>) parent.getBuilder().getField(field));
                serviceConfigList.remove(item.getParent().getChildren().indexOf(item));
                parent.getBuilder().clearField(field);
                parent.getBuilder().setField(field, serviceConfigList);

                item.setSendableChanged();
                item.getParent().getChildren().remove(item);
            }
        }
    }
}
