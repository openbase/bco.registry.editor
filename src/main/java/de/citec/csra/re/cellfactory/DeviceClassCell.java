/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.csra.re.RegistryEditor;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.struct.node.DeviceClassContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jul.exception.CouldNotPerformException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import rst.homeautomation.device.DeviceClassType.DeviceClass;

/**
 *
 * @author thuxohl
 */
public class DeviceClassCell extends ValueCell {

    public DeviceClassCell(DeviceRegistryRemote deviceRegistryRemote, LocationRegistryRemote locationRegistryRemote) {
        super(deviceRegistryRemote, locationRegistryRemote);

        applyButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Thread thread = new Thread(
                        new Task<Boolean>() {
                            @Override
                            protected Boolean call() throws Exception {
                                RegistryEditor.setModified(false);
                                DeviceClassContainer container = (DeviceClassContainer) getItem();

                                DeviceClass deviceClass = container.getBuilder().build();
                                try {
                                    if (deviceRegistryRemote.containsDeviceClass(deviceClass)) {
                                        deviceRegistryRemote.updateDeviceClass(deviceClass);
                                    } else {
                                        deviceRegistryRemote.registerDeviceClass(deviceClass);
                                        container.setNewNode(false);
                                    }
                                    container.setChanged(false);
                                } catch (CouldNotPerformException ex) {
                                    logger.warn("Could not register or update device class [" + deviceClass + "]", ex);
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
                                DeviceClassContainer container = (DeviceClassContainer) getItem();

                                DeviceClass deviceClass = container.getBuilder().build();
                                try {
                                    if (container.getNewNode()) {
                                        container.getParent().getChildren().remove(container);
                                    } else {
                                        int index = container.getParent().getChildren().indexOf(container);
                                        container.getParent().getChildren().set(index, new DeviceClassContainer(deviceRegistryRemote.getDeviceClassById(deviceClass.getId()).toBuilder()));
                                    }
                                } catch (CouldNotPerformException ex) {
                                    logger.warn("Could cancel update of [" + deviceClass + "]", ex);
                                }
                                return true;
                            }
                        });
                thread.setDaemon(true);
                thread.start();
            }
        });
    }

    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (item instanceof DeviceClassContainer) {
            DeviceClassContainer container = (DeviceClassContainer) item;
            if ((container.getNewNode() || container.hasChanged()) && getGraphic() != buttonBox) {
                System.out.println("Setting buttons on [" + item.getDescriptor() + "] case1");
                setGraphic(buttonBox);
            } else if (!(container.getNewNode() || container.hasChanged()) && getGraphic() != null) {
                setGraphic(null);
                System.out.println("Resetting buttons on [" + item.getDescriptor() + "] case1");
            }
            container.getChanged().addListener(new ChangeListener<Boolean>() {

                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            if (newValue && getGraphic() != buttonBox) {
                                System.out.println("Setting buttons on [" + item.getDescriptor() + "] case2");
                                setGraphic(buttonBox);
                            } else if (getGraphic() != null) {
                                System.out.println("Resetting buttons on [" + item.getDescriptor() + "] case2");
                                setGraphic(null);
                            }
                        }
                    });
                }
            });
        } else {
            if (item != null && ((!"scope".equals(item.getDescriptor()) && (!"id".equals(item.getDescriptor()))))) {
                setGraphic(null);
            }
        }
    }
}
