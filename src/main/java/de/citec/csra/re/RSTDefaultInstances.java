/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re;

import java.util.Date;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.state.InventoryStateType.InventoryState;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.math.Vec3DFloatType.Vec3DFloat;
import rst.spatial.PlacementConfigType.PlacementConfig;
import rst.timing.TimestampType.Timestamp;

/**
 *
 * @author thuxohl
 */
public class RSTDefaultInstances {

    public static DeviceConfig.Builder getDefaultDeviceConfig() {
        PlacementConfig placementConfig = PlacementConfig.newBuilder().setPosition(Vec3DFloat.newBuilder().setX(0).setY(0).setZ(0).build()).build();
        InventoryState inventoryState = InventoryState.newBuilder().setTimestamp(Timestamp.newBuilder().setTime((new Date()).toInstant().toEpochMilli()).build()).build();
        return DeviceConfig.newBuilder().setPlacementConfig(placementConfig).setInventoryState(inventoryState);
    }
    
    public static UnitConfig.Builder getDefaultUnitConfig() {
        PlacementConfig placementConfig = PlacementConfig.newBuilder().setPosition(Vec3DFloat.newBuilder().setX(0).setY(0).setZ(0).build()).build();
        return UnitConfig.newBuilder().setPlacement(placementConfig);
    }
}
