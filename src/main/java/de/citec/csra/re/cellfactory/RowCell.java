/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import de.citec.agm.remote.AgentRegistryRemote;
import de.citec.apm.remote.AppRegistryRemote;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.util.RSTDefaultInstances;
import de.citec.csra.re.RegistryEditor;
import de.citec.csra.re.struct.Leaf;
import de.citec.csra.re.struct.LeafContainer;
import de.citec.csra.re.struct.GenericListContainer;
import de.citec.csra.re.struct.Node;
import de.citec.csra.re.struct.NodeContainer;
import de.citec.csra.re.struct.SendableNode;
import de.citec.csra.re.struct.VariableNode;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.ExceptionPrinter;
import de.citec.scm.remote.SceneRegistryRemote;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableCell;
import org.slf4j.LoggerFactory;
import rst.homeautomation.control.agent.AgentConfigType;
import rst.homeautomation.control.app.AppConfigType;
import rst.homeautomation.control.scene.SceneConfigType;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.service.ServiceConfigType;
import rst.homeautomation.service.ServiceTypeHolderType.ServiceTypeHolder.ServiceType;
import rst.homeautomation.unit.UnitConfigType;
import rst.spatial.LocationConfigType.LocationConfig;
import rst.spatial.PlacementConfigType;

/**
 * Cell factory to manage similar options for all cells in a row. Initializes
 * and manages the context menu for all child cells.
 *
 * @author thuxohl
 */
public abstract class RowCell extends TreeTableCell<Node, Node> {

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

//    public static final String BACKGROUND_COLOR = "-fx-text-fill: black;-fx-background-color: ";
    protected final DeviceRegistryRemote deviceRegistryRemote;
    protected final LocationRegistryRemote locationRegistryRemote;
    protected final SceneRegistryRemote sceneRegistryRemote;
    protected final AgentRegistryRemote agentRegistryRemote;
    protected final AppRegistryRemote appRegistryRemote;

    private final ContextMenu contextMenu;
    private final MenuItem addMenuItem, removeMenuItem;

    public RowCell(DeviceRegistryRemote deviceRegistryRemote, LocationRegistryRemote locationRegistryRemote, SceneRegistryRemote sceneRegistryRemote, AgentRegistryRemote agentRegistryRemote, AppRegistryRemote appRegistryRemote) {
        this.deviceRegistryRemote = deviceRegistryRemote;
        this.locationRegistryRemote = locationRegistryRemote;
        this.sceneRegistryRemote = sceneRegistryRemote;
        this.agentRegistryRemote = agentRegistryRemote;
        this.appRegistryRemote = appRegistryRemote;
        addMenuItem = new MenuItem("Add");
        removeMenuItem = new MenuItem("Remove");
        EventHandlerImpl eventHandler = new EventHandlerImpl();
        addMenuItem.setOnAction(eventHandler);
        removeMenuItem.setOnAction(eventHandler);
        contextMenu = new ContextMenu(addMenuItem, removeMenuItem);
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
        } else if (item instanceof VariableNode) {
            addMenuItem.setVisible(true);
            removeMenuItem.setVisible(true);
            setContextMenu(contextMenu);
        } else if (item instanceof Leaf) {
            if (((Leaf) item).getValue() instanceof ServiceType) {
                addMenuItem.setVisible(true);
                removeMenuItem.setVisible(true);
                setContextMenu(contextMenu);
            } else if (item.getDescriptor().equals("unit_id")) {
                addMenuItem.setVisible(true);
                removeMenuItem.setVisible(true);
                setContextMenu(contextMenu);
            } else if (item.getDescriptor().equals("child_id")) {
                removeMenuItem.setVisible(true);
                addMenuItem.setVisible(false);
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
            // TODO Tamino: implement ExceptionHanlding
            try {
                RegistryEditor.setModified(true);
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
//                    listNode.setExpanded(true);
//                    listNode.setSendableChanged();
//                } else if (add instanceof ServiceTypeListContainer) {
//                    ServiceTypeListContainer listNode = ((ServiceTypeListContainer) add);
//                    UnitTemplate.Builder unitTemplate = listNode.getBuilder().addServiceType(ServiceType.UNKNOWN);
//                    listNode.add(ServiceType.UNKNOWN, "service_type", listNode.getBuilder().getServiceTypeList().size() - 1);
//                    listNode.setExpanded(true);
//                    listNode.setSendableChanged();
//                } else if (add instanceof UnitIdListContainer) {
//                    UnitIdListContainer listNode = (UnitIdListContainer) add;
//                    listNode.getBuilder().addUnitId("");
//                    listNode.add("", "unit_id", listNode.getBuilder().getUnitIdList().size() - 1);
//                    listNode.setExpanded(true);
//                    listNode.setSendableChanged();
//                } else if (add instanceof Leaf) {
//                    if (((Leaf) add).getValue() instanceof ServiceType) {
//                        ServiceTypeListContainer listNode = ((ServiceTypeListContainer) ((LeafContainer) add).getParent());
//                        UnitTemplate.Builder unitTemplate = listNode.getBuilder().addServiceType(ServiceType.UNKNOWN);
//                        listNode.add(ServiceType.UNKNOWN, "service_type", listNode.getBuilder().getServiceTypeList().size() - 1);
//                        listNode.setExpanded(true);
//                        listNode.setSendableChanged();
//                    } else if (((Leaf) add).getDescriptor().equals("unit_id")) {
//                        UnitIdListContainer listNode = ((UnitIdListContainer) ((LeafContainer) add).getParent());
//                        listNode.getBuilder().addUnitId("");
//                        listNode.add("", "unit_id", listNode.getBuilder().getUnitIdList().size() - 1);
//                        listNode.setExpanded(true);
//                        listNode.setSendableChanged();
//                    } else if (((Leaf) add).getValue() instanceof UnitType) {
//                        UnitTemplateConfigListContainer listNode = ((UnitTemplateConfigListContainer) ((LeafContainer) add).getParent());
//                        UnitTemplateConfig.Builder unitTemplate = listNode.getBuilder().addUnitTemplateConfigBuilder();
//                        listNode.add(new UnitTemplateConfigContainer(unitTemplate));
//                        listNode.setExpanded(true);
//                        listNode.setSendableChanged();
//                    }
//                } else if (add instanceof ChildLocationListContainer) {
//                    ChildLocationListContainer listNode = ((ChildLocationListContainer) add);
//                    listNode.getBuilder().addChildId("");
//                    listNode.add("", "child_id", listNode.getBuilder().getChildIdCount() - 1);
//                    listNode.setExpanded(true);
//                    listNode.setSendableChanged();
//                } else if (add instanceof LocationConfigContainer) {
//                    LocationConfigListContainer listNode = (LocationConfigListContainer) ((LocationConfigContainer) add).getParent();
//                    LocationConfig.Builder locationConfigBuilder = listNode.getBuilder().addLocationConfigBuilder().setRoot(true);
//                    locationConfigBuilder.setPosition(RSTDefaultInstances.getDefaultPose());
//                    LocationConfigContainer rootLocation = new LocationConfigContainer(locationConfigBuilder);
//                    listNode.add(rootLocation);
//                    listNode.setExpanded(true);
//                    rootLocation.setExpanded(true);
//                    rootLocation.setNewNode(true);
//                    rootLocation.setChanged(true);
//                } else if (add instanceof DeviceConfigGroupContainer) {
//                    DeviceConfigGroupContainer parentNode = (DeviceConfigGroupContainer) add;
//                    DeviceConfig.Builder deviceConfig = RSTDefaultInstances.getDefaultDeviceConfig();
//                    deviceConfig.setDeviceClassId(parentNode.getDeviceClass().getId());
//                    addUnitConfigs(deviceConfig, parentNode.getDeviceClass());
//                    DeviceConfigContainer addedNode = new DeviceConfigContainer(deviceConfig);
//                    addedNode.setChanged(true);
//                    addedNode.setNewNode(true);
//                    addedNode.setExpanded(true);
//                    parentNode.setExpanded(true);
//                    parentNode.getChildren().add(addedNode);
//                } else if (add instanceof LocationGroupContainer) {
//                    LocationGroupContainer parentNode = (LocationGroupContainer) add;
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
//                } else if (add instanceof ServiceTemplateContainer) {
//                    ServiceTemplateContainer parentNode = (ServiceTemplateContainer) add;
//                    parentNode.addNewDefaultElement();
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
            if (nodeToRemove instanceof SendableNode) {
                SendableNode sendable = (SendableNode) nodeToRemove;
                if (!sendable.getNewNode()) {
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
                        } else if (message instanceof SceneConfigType.SceneConfig) {
                            if (sceneRegistryRemote.containsSceneConfig((SceneConfigType.SceneConfig) message)) {
                                sceneRegistryRemote.removeSceneConfig((SceneConfigType.SceneConfig) message);
                            }
                        } else if (message instanceof AgentConfigType.AgentConfig) {
                            if (agentRegistryRemote.containsAgentConfig((AgentConfigType.AgentConfig) message)) {
                                agentRegistryRemote.removeAgentConfig((AgentConfigType.AgentConfig) message);
                            }
                        } else if (message instanceof AppConfigType.AppConfig) {
                            if (appRegistryRemote.containsAppConfig((AppConfigType.AppConfig) message)) {
                                appRegistryRemote.removeAppConfig((AppConfigType.AppConfig) message);
                            }
                        }
                    } catch (CouldNotPerformException ex) {
                        logger.warn("Could not remove sendable [" + sendable.getBuilder() + "]", ex);
                    }
                }
                sendable.getParent().getChildren().remove(sendable);
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
        Descriptors.FieldDescriptor field = parent.getBuilder().getDescriptorForType().findFieldByName(parent.getDescriptor().substring(0, parent.getDescriptor().length() - 1));
        if (parent.getDescriptor().equals("meta_configs")) {
            field = parent.getBuilder().getDescriptorForType().findFieldByName("entry");
        }
        List updatedList = new ArrayList((List) parent.getBuilder().getField(field));
        updatedList.remove(index);
        parent.getBuilder().clearField(field);
        parent.getBuilder().setField(field, updatedList);
        parent.setSendableChanged();
        parent.getChildren().remove(index);
    }

}
