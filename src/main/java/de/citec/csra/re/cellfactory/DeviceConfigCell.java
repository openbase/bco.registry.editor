/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.csra.dm.remote.DeviceRegistryRemote;
import de.citec.csra.re.struct.leaf.Leaf;
import de.citec.csra.re.struct.node.DeviceClassContainer;
import de.citec.csra.re.struct.node.DeviceConfigContainer;
import de.citec.csra.re.struct.node.LocationConfigContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.NotAvailableException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.spatial.LocationConfigType.LocationConfig;

/**
 *
 * @author thuxohl
 */
public class DeviceConfigCell extends ValueCell {

    private final ComboBox<DeviceClass> deviceClassComboBox;
    private final ComboBox<LocationConfig> locationConfigComboBox;
    
    public DeviceConfigCell(DeviceRegistryRemote remote) {
        super(remote);
        
        applyButton.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                DeviceConfig deviceConfig = ((DeviceConfigContainer) getItem()).getBuilder().build();
                try {
                    if (remote.containsDeviceConfig(deviceConfig)) {
                        remote.updateDeviceConfig(deviceConfig);
                    } else {
                        remote.registerDeviceConfig(deviceConfig);
                    }
                } catch (CouldNotPerformException ex) {
                    logger.warn("Could not register or update device config [" + deviceConfig + "]", ex);
                }
            }
        });
        
        deviceClassComboBox = new ComboBox();
        deviceClassComboBox.setCellFactory(new Callback<ListView<DeviceClass>, ListCell<DeviceClass>>() {

            @Override
            public ListCell<DeviceClass> call(ListView<DeviceClass> param) {
                return new DeviceClassComboBoxCell();
            }
        });
        deviceClassComboBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Leaf test = (Leaf<DeviceClass>) getItem();
                test.setValue(deviceClassComboBox.getValue());
            }
        });
        
        locationConfigComboBox = new ComboBox<>();
        locationConfigComboBox.setCellFactory(new Callback<ListView<LocationConfig>, ListCell<LocationConfig>>() {

            @Override
            public ListCell<LocationConfig> call(ListView<LocationConfig> param) {
                return new LocationConfigComboBoxCell();
            }
        });
        locationConfigComboBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                ((Leaf<LocationConfig>) getItem()).setValue(locationConfigComboBox.getValue());
            }
        });
    }
    
    @Override
    public void startEdit() {
        super.startEdit();
        
        if( getItem() instanceof DeviceConfigContainer ) {
            try {
                deviceClassComboBox.setItems(FXCollections.observableArrayList(remote.getData().getDeviceClassesList()));
            } catch (NotAvailableException ex) {
                logger.warn("Could not receive data to fill the deviceClassComboBox", ex);
            }
        } else if( getItem() instanceof LocationConfigContainer ) {
//            locationConfigComboBox.setItems(FXCollections.observableArrayList(remote.getData().getDeviceClassesList()));
            // TODO implement if locationConfig is ready
        }
    }
    
    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if( item instanceof DeviceConfigContainer) {
            setGraphic(applyButton);
        }
    }
    
    private class DeviceClassComboBoxCell extends ListCell<DeviceClass> {
        
        @Override
        public void updateItem(DeviceClass item, boolean empty) {
            super.updateItem(item, empty);
            
            if(!empty) {
                setText(item.getId());
            }
        }
    }
    
    private class LocationConfigComboBoxCell extends ListCell<LocationConfig> {
        
        @Override
        public void updateItem(LocationConfig item, boolean empty) {
            super.updateItem(item, empty);
            
            if(!empty) {
                setText(item.getLabel());
            }
        }
    }
}
