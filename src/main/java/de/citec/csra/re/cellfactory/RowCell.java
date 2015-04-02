/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import de.citec.csra.dm.remote.DeviceRegistryRemote;
import de.citec.csra.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.RSTDefaultInstances;
import de.citec.csra.re.struct.leaf.Leaf;
import de.citec.csra.re.struct.leaf.LeafContainer;
import de.citec.csra.re.struct.node.ChildLocationListContainer;
import de.citec.csra.re.struct.node.DeviceClassContainer;
import de.citec.csra.re.struct.node.DeviceConfigContainer;
import de.citec.csra.re.struct.node.LocationConfigContainer;
import de.citec.csra.re.struct.node.LocationConfigListContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.csra.re.struct.node.NodeContainer;
import de.citec.csra.re.struct.node.SendableNode;
import de.citec.csra.re.struct.node.ServiceConfigContainer;
import de.citec.csra.re.struct.node.ServiceConfigListContainer;
import de.citec.csra.re.struct.node.UnitConfigContainer;
import de.citec.csra.re.struct.node.UnitConfigIdListContainer;
import de.citec.csra.re.struct.node.UnitConfigListContainer;
import de.citec.csra.re.struct.node.UnitTypeListContainer;
import de.citec.csra.re.struct.node.VariableNode;
import de.citec.jul.exception.CouldNotPerformException;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
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
import rst.spatial.LocationConfigType.LocationConfig;

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

        if ((item instanceof UnitTypeListContainer) || (item instanceof UnitConfigListContainer) || (item instanceof ServiceConfigListContainer) || (item instanceof ChildLocationListContainer) || (item instanceof UnitConfigIdListContainer)) {
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
            } else if (item.getDescriptor().equals("unit_config_ids")) {
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
            Thread thread = new Thread(
                    new Task<Boolean>() {

                        @Override
                        protected Boolean call() throws Exception {
                            if (event.getSource().equals(addMenuItem)) {
                                addAction(RowCell.this.getItem());
                            } else if (event.getSource().equals(removeMenuItem)) {
                                removeAction(RowCell.this.getItem());
                            }
                            return true;
                        }

                    });
            thread.setDaemon(true);
            thread.start();
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
                DeviceConfigContainer addedNode = new DeviceConfigContainer(RSTDefaultInstances.getDefaultDeviceConfig());
                addedNode.setChanged(true);
                addedNode.setNewNode(true);
                addedNode.setExpanded(true);
                parent.setExpanded(true);
                parent.getChildren().add(addedNode);
            } else if (add instanceof UnitTypeListContainer) {
                UnitTypeListContainer listNode = ((UnitTypeListContainer) add);
                UnitTypeHolder.Builder unitTypeBuilder = listNode.getBuilder().addUnitsBuilder();
                listNode.add(unitTypeBuilder.getUnitType(), "unit", listNode.getBuilder().getUnitsBuilderList().indexOf(unitTypeBuilder));
                listNode.setExpanded(true);
                listNode.setSendableChanged();
            } else if (add instanceof UnitConfigIdListContainer) {
                UnitConfigIdListContainer listNode = (UnitConfigIdListContainer) add;
                listNode.getBuilder().addUnitConfigIds("");
                listNode.add("", "unit_config_id", listNode.getBuilder().getUnitConfigIdsList().indexOf(""));
                listNode.setExpanded(true);
                listNode.setSendableChanged();
            } else if (add instanceof Leaf) {
                if (((Leaf) add).getValue() instanceof UnitTypeHolder.UnitType) {
                    UnitTypeListContainer listNode = ((UnitTypeListContainer) ((LeafContainer) add).getParent());
                    UnitTypeHolder.Builder unitTypeBuilder = listNode.getBuilder().addUnitsBuilder();
                    listNode.add(unitTypeBuilder.getUnitType(), "unit", listNode.getBuilder().getUnitsBuilderList().indexOf(unitTypeBuilder));
                    listNode.setExpanded(true);
                    listNode.setSendableChanged();
                } else if (((Leaf) add).getDescriptor().equals("unit_config_id")) {
                    UnitConfigIdListContainer listNode = ((UnitConfigIdListContainer) ((LeafContainer) add).getParent());
                    listNode.getBuilder().addUnitConfigIds("");
                    listNode.add("", "unit_config_id", listNode.getBuilder().getUnitConfigIdsList().indexOf(""));
                    listNode.setExpanded(true);
                    listNode.setSendableChanged();
                }
            } else if (add instanceof UnitConfigListContainer) {
                UnitConfigListContainer listNode = ((UnitConfigListContainer) add);
                UnitConfig.Builder unitConfigBuilder = RSTDefaultInstances.getDefaultUnitConfig();
                listNode.getBuilder().addUnitConfigs(unitConfigBuilder);
                listNode.add(new UnitConfigContainer(unitConfigBuilder));
                listNode.setExpanded(true);
                listNode.setSendableChanged();
            } else if (add instanceof UnitConfigContainer) {
                UnitConfigListContainer listNode = ((UnitConfigListContainer) ((UnitConfigContainer) add).getParent());
                UnitConfig.Builder unitConfigBuilder = RSTDefaultInstances.getDefaultUnitConfig();
                listNode.getBuilder().addUnitConfigs(unitConfigBuilder);
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
            } else if (add instanceof ChildLocationListContainer) {
                ChildLocationListContainer listNode = ((ChildLocationListContainer) add);
                LocationConfig.Builder locationConfigBuilder = listNode.getBuilder().addChildrenBuilder().setRoot(false);
                locationConfigBuilder.setParentId(listNode.getBuilder().getId());
                listNode.add(new LocationConfigContainer(locationConfigBuilder));
                listNode.setExpanded(true);
                listNode.setSendableChanged();
            } else if (add instanceof LocationConfigContainer) {
                if (((LocationConfigContainer) add).getParent() instanceof ChildLocationListContainer) {
                    ChildLocationListContainer listNode = (ChildLocationListContainer) ((LocationConfigContainer) add).getParent();
                    LocationConfig.Builder locationConfigBuilder = listNode.getBuilder().addChildrenBuilder().setRoot(false);
                    locationConfigBuilder.setParentId(listNode.getBuilder().getId());
                    listNode.add(new LocationConfigContainer(locationConfigBuilder));
                    listNode.setExpanded(true);
                    listNode.setSendableChanged();
                } else {
                    LocationConfigListContainer listNode = (LocationConfigListContainer) ((LocationConfigContainer) add).getParent();
                    LocationConfig.Builder locationConfigBuilder = listNode.getBuilder().addLocationConfigsBuilder().setRoot(true);
                    LocationConfigContainer rootLocation = new LocationConfigContainer(locationConfigBuilder);
                    listNode.add(rootLocation);
                    listNode.setExpanded(true);
                    rootLocation.setExpanded(true);
                    rootLocation.setNewNode(true);
                    rootLocation.setChanged(true);
                }
            }
        }

        private void removeAction(Node nodeToRemove) {
            // check if the removed item is an instance of classes that have to be directly
            // removed in the registry [deviceClass, deviceConfig, locationConfig]
            if (nodeToRemove instanceof SendableNode) {
                SendableNode sendable = (SendableNode) nodeToRemove;
                if (sendable.getNewNode()) {
                    sendable.getParent().getChildren().remove(sendable);
                } else {
                    try {
                        Message message = sendable.getBuilder().build();
                        if (message instanceof DeviceClass) {
                            if (deviceRegistryRemote.containsDeviceClass((DeviceClass) message)) {
                                deviceRegistryRemote.removeDeviceClass((DeviceClass) message);
                            }
                        } else if (message instanceof DeviceConfig) {
                            if (deviceRegistryRemote.containsDeviceConfig((DeviceConfig) message)) {
                                deviceRegistryRemote.removeDeviceConfig((DeviceConfig) message);
                            }
                        } else if (message instanceof LocationConfig) {
                            if (locationRegistryRemote.containsLocationConfig((LocationConfig) message)) {
                                locationRegistryRemote.removeLocationConfig((LocationConfig) message);
                            }
                        }
                        sendable.getParent().getChildren().remove(sendable);
                    } catch (CouldNotPerformException ex) {
                        logger.info("Could not remove sendable [" + sendable.getBuilder() + "]", ex);
                    }
                }
            } else if (nodeToRemove instanceof Leaf) {
                removeNodeFromRepeatedField(((LeafContainer) nodeToRemove).getParent(), ((LeafContainer) nodeToRemove).getIndex());
            } else {
                removeNodeFromRepeatedField((NodeContainer) ((NodeContainer) nodeToRemove).getParent().getValue(), nodeToRemove);
            }
        }
    }

    private void removeNodeFromRepeatedField(NodeContainer parent, Node nodeToRemove) {
        removeNodeFromRepeatedField(parent, parent.getChildren().indexOf(nodeToRemove));
    }

    private void removeNodeFromRepeatedField(NodeContainer parent, int index) {
        Descriptors.FieldDescriptor field = parent.getBuilder().getDescriptorForType().findFieldByName(parent.getDescriptor());
        List updatedList = new ArrayList((List) parent.getBuilder().getField(field));
        updatedList.remove(index);
        parent.getBuilder().clearField(field);
        parent.getBuilder().setField(field, updatedList);
        parent.setSendableChanged();
        parent.getChildren().remove(index);
    }

}
