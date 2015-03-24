/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.csra.dm.remote.DeviceRegistryRemote;
import de.citec.csra.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.struct.node.LocationConfigContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.jul.exception.CouldNotPerformException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author thuxohl
 */
public class LocationConfigCell extends ValueCell {

    public LocationConfigCell(DeviceRegistryRemote deviceRegistryRemote, LocationRegistryRemote locationRegistryRemote) {
        super(deviceRegistryRemote, locationRegistryRemote);

        applyButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                LocationConfigContainer container = (LocationConfigContainer) getItem();
                LocationConfig locationConfig = container.getBuilder().build();
                try {
                    if (locationRegistryRemote.containsLocationConfig(locationConfig)) {
                        locationRegistryRemote.updateLocationConfig(locationConfig);
                    } else {
                        locationRegistryRemote.registerLocationConfig(locationConfig);
                        container.setNewNode(false);
                    }
                    container.setChanged(false);
                } catch (CouldNotPerformException ex) {
                    logger.warn("Could not register or update device class [" + locationConfig + "]", ex);
                }
            }
        });
    }

    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (item instanceof LocationConfigContainer && ((LocationConfigContainer) item).getBuilder().getRoot()) {
            setGraphic(applyButton);
            ((LocationConfigContainer) item).getChanged().addListener(new ChangeListener<Boolean>() {

                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    applyButton.setVisible(newValue);
                }
            });
        }
    }
}
