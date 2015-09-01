/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.csra.re.RegistryEditor;
import de.citec.csra.re.struct.node.DeviceClassContainer;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jul.exception.CouldNotPerformException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import rst.homeautomation.device.DeviceClassType.DeviceClass;

/**
 *
 * @author thuxohl
 */
public class DeviceClassCell extends ValueCell {

    private final String defaultButtonStyle;

    public DeviceClassCell(DeviceRegistryRemote deviceRegistryRemote) {
        super(deviceRegistryRemote, null, null, null, null);
        defaultButtonStyle = applyButton.getStyle();
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
                                applyButton.setStyle(defaultButtonStyle);
                                applyButton.setText("Apply");
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
}
