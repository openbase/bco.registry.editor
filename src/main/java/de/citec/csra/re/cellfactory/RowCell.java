/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import de.citec.csra.re.RegistryEditor;
import de.citec.csra.re.struct.GenericGroupContainer;
import de.citec.csra.re.struct.Leaf;
import de.citec.csra.re.struct.LeafContainer;
import de.citec.csra.re.struct.GenericListContainer;
import de.citec.csra.re.struct.GenericNodeContainer;
import de.citec.csra.re.struct.Node;
import de.citec.csra.re.struct.NodeContainer;
import de.citec.csra.re.util.FieldGroup;
import de.citec.csra.re.util.RSTDefaultInstances;
import de.citec.csra.re.util.RemotePool;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.ExceptionPrinter;
import de.citec.jul.exception.InstantiationException;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableCell;
import org.slf4j.LoggerFactory;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.unit.UnitConfigType;
import rst.spatial.PlacementConfigType;

/**
 * Cell factory to manage similar options for all cells in a row. Initializes
 * and manages the context menu for all child cells.
 *
 * @author thuxohl
 */
public abstract class RowCell extends TreeTableCell<Node, Node> {

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    protected RemotePool remotePool;
    private final ContextMenu contextMenu;
    private final MenuItem addMenuItem, removeMenuItem;

    public RowCell() {
        try {
            remotePool = RemotePool.getInstance();
        } catch (InstantiationException ex) {
            ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
        }

        addMenuItem = new MenuItem("Add");
        removeMenuItem = new MenuItem("Remove");
        contextMenu = new ContextMenu(addMenuItem, removeMenuItem);

        EventHandlerImpl eventHandler = new EventHandlerImpl();
        addMenuItem.setOnAction(eventHandler);
        removeMenuItem.setOnAction(eventHandler);

        this.setFocused(true);
        this.setEditable(true);
    }

    @Override
    protected void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (item instanceof GenericListContainer) {
            addMenuItem.setVisible(true);
            removeMenuItem.setVisible(false);
            setContextMenu(contextMenu);
        } else if ((item instanceof NodeContainer && ((NodeContainer) item).getParent().getValue() instanceof GenericListContainer)
                || (item instanceof LeafContainer && ((LeafContainer) item).getParent() instanceof GenericListContainer)) {
            addMenuItem.setVisible(true);
            removeMenuItem.setVisible(true);
            setContextMenu(contextMenu);
            //TODO: maybe there are cases where lists cannot be edited, elements only removable or addable?
//        } else if (item instanceof Leaf) {
//            if (((Leaf) item).getValue() instanceof ServiceType) {
//                addMenuItem.setVisible(true);
//                removeMenuItem.setVisible(true);
//                setContextMenu(contextMenu);
//            } else if (item.getDescriptor().equals("unit_id")) {
//                addMenuItem.setVisible(true);
//                removeMenuItem.setVisible(true);
//                setContextMenu(contextMenu);
//            } else if (item.getDescriptor().equals("child_id")) {
//                removeMenuItem.setVisible(true);
//                addMenuItem.setVisible(false);
//                setContextMenu(contextMenu);
//            }
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
            // TODO Tamino: implement ExceptionHanlding
            try {
                RegistryEditor.setModified(true);
                if (add instanceof GenericNodeContainer) {
                    GeneratedMessage.Builder builder = ((NodeContainer) add).getBuilder();
                    GenericListContainer parent = (GenericListContainer) ((NodeContainer) add).getParent().getValue();

                    List<FieldGroup> groups = new ArrayList<>();
                    NodeContainer groupContainer = parent;
                    while (groupContainer.getParent() != null && groupContainer.getParent().getValue() instanceof GenericGroupContainer) {
                        groupContainer = (GenericGroupContainer) groupContainer.getParent().getValue();
                        groups.add(((GenericGroupContainer) groupContainer).getFieldGroup());
                    }

                    GeneratedMessage.Builder addedBuilder = RSTDefaultInstances.getDefaultBuilder(builder);
                    for(FieldGroup group: groups) {
                        group.setValue(addedBuilder, group);
                    }
                    parent.addElement(addedBuilder);
                } else if(add instanceof LeafContainer) {
                    GenericListContainer parentNode = (GenericListContainer) ((LeafContainer) add).getParent();
                    parentNode.addNewDefaultElement();
                }
//                if (add instanceof DeviceClassContainer) {
//                    NodeContainer parent = (NodeContainer) ((DeviceClassContainer) add).getParent().getValue();
//                    DeviceClassContainer addedNode = new DeviceClassContainer(DeviceClass.getDefaultInstance().toBuilder());
//                    addedNode.setChanged(true);
//                    addedNode.setNewNode(true);
//                    addedNode.setExpanded(true);
//                    parent.setExpanded(true);
//                    parent.getChildren().add(addedNode);
//                } else if (add instanceof DeviceConfigContainer) {
//                    LocationGroupContainer parentNode = (LocationGroupContainer) ((DeviceConfigContainer) add).getParent();
//                    DeviceClass deviceClass = ((DeviceConfigGroupContainer) parentNode.getParent()).getDeviceClass();
//                    DeviceConfig.Builder deviceConfig = RSTDefaultInstances.getDefaultDeviceConfig();
//                    deviceConfig.setDeviceClassId(deviceClass.getId());
//                    addUnitConfigs(deviceConfig, deviceClass);
//                    deviceConfig.getPlacementConfigBuilder().setLocationId(parentNode.getLocationId());
//                    addLocationIDToAllUnits(deviceConfig, parentNode.getLocationId());
//                    DeviceConfigContainer addedNode = new DeviceConfigContainer(deviceConfig);
//                    addedNode.setChanged(true);
//                    addedNode.setNewNode(true);
//                    addedNode.setExpanded(true);
//                    parentNode.setExpanded(true);
//                    parentNode.getChildren().add(addedNode);
//                } else if (add instanceof UnitTemplateConfigListContainer) {
//                    UnitTemplateConfigListContainer listNode = ((UnitTemplateConfigListContainer) add);
//                    UnitTemplateConfig.Builder unitTemplate = listNode.getBuilder().addUnitTemplateConfigBuilder();
//                    listNode.add(new UnitTemplateConfigContainer(unitTemplate));
//                    listNode.setExpanded(true);
//                    listNode.setSendableChanged();
//                } else if (add instanceof UnitTemplateConfigContainer) {
//                    UnitTemplateConfigListContainer listNode = ((UnitTemplateConfigListContainer) ((NodeContainer) add).getParent().getValue());
//                    UnitTemplateConfig.Builder unitTemplateConfig = listNode.getBuilder().addUnitTemplateConfigBuilder();
//                    listNode.add(new UnitTemplateConfigContainer(unitTemplateConfig));
//                    listNode.setExpande
                if (add instanceof GenericListContainer) {
                    GenericListContainer parentNode = (GenericListContainer) add;
                    parentNode.addNewDefaultElement();
                }
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory(logger, ex);
            }
        }

//        private void addUnitConfigs(DeviceConfig.Builder deviceConfig, DeviceClass deviceClass) {
//            deviceConfig.clearUnitConfig();
//            deviceClass.getUnitTemplateList().stream().map((unitTemplateConfig) -> {
//                UnitConfigType.UnitConfig.Builder unitConfigBuilder = UnitConfigType.UnitConfig.newBuilder().setType(unitTemplateConfig.getType());
//                unitTemplateConfig.getServiceTemplateList().stream().forEach((serviceTemplate) -> {
//                    unitConfigBuilder.addServiceConfig(ServiceConfigType.ServiceConfig.newBuilder().setType(serviceTemplate.getServiceType()));
//                });
//                return unitConfigBuilder;
//            }).forEach((unitConfigBuilder) -> {
//                deviceConfig.addUnitConfig(RSTDefaultInstances.setDefaultPlacement(unitConfigBuilder).build());
//            });
//        }
        private void addLocationIDToAllUnits(DeviceConfig.Builder deviceConfig, String locationId) {
            List<UnitConfigType.UnitConfig.Builder> unitBuilder = deviceConfig.getUnitConfigBuilderList();
            List<UnitConfigType.UnitConfig> units = new ArrayList<>();
            for (UnitConfigType.UnitConfig.Builder unit : unitBuilder) {
                PlacementConfigType.PlacementConfig placement = unit.getPlacementConfigBuilder().setLocationId(locationId).build();
                units.add(unit.setPlacementConfig(placement).clone().build());
            }
            deviceConfig.clearUnitConfig();
            deviceConfig.addAllUnitConfig(units);
        }

        private void removeAction(Node nodeToRemove) {
            RegistryEditor.setModified(true);
            // check if the removed item is an instance of classes that have to be directly
            // removed in the registry [deviceClass, deviceConfig, locationConfig]
            if (nodeToRemove instanceof NodeContainer && ((NodeContainer) nodeToRemove).isSendable()) {
                NodeContainer removed = (NodeContainer) nodeToRemove;
                try {
                    Message message = removed.getBuilder().build();
                    if (remotePool.contains(message)) {
                        remotePool.remove(message);
                    }
                } catch (CouldNotPerformException ex) {
                    ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
                }
                removed.getParent().getChildren().remove(removed);
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
        Descriptors.FieldDescriptor field = parent.getFieldDescriptor();
        List updatedList = new ArrayList((List) parent.getBuilder().getField(field));
        updatedList.remove(index);
        parent.getBuilder().clearField(field);
        parent.getBuilder().setField(field, updatedList);
        parent.setSendableChanged();
        parent.getChildren().remove(index);
    }
}
