/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re;

import java.util.Date;
import rst.geometry.PoseType.Pose;
import rst.geometry.RotationType.Rotation;
import rst.geometry.TranslationType.Translation;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.state.InventoryStateType.InventoryState;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.spatial.PlacementConfigType.PlacementConfig;
import rst.timing.TimestampType.Timestamp;

/**
 *
 * @author thuxohl
 */
public class RSTDefaultInstances {

    public static DeviceConfig.Builder getDefaultDeviceConfig() {
        PlacementConfig placementConfig = PlacementConfig.newBuilder().setPosition(getDefaultPose()).build();
        InventoryState inventoryState = InventoryState.newBuilder().setTimestamp(Timestamp.newBuilder().setTime((new Date()).toInstant().toEpochMilli()).build()).build();
        return DeviceConfig.newBuilder().setPlacementConfig(placementConfig).setInventoryState(inventoryState);
    }

    public static UnitConfig.Builder setDefaultPlacement(UnitConfig.Builder unitBuilder) {
        PlacementConfig placementConfig = PlacementConfig.newBuilder().setPosition(getDefaultPose()).build();
        return unitBuilder.setPlacementConfig(placementConfig);
    }

    private static Pose getDefaultPose() {
        Rotation rotation = Rotation.newBuilder().setQw(1).setQx(0).setQy(0).setQz(0).build();
        Translation translation = Translation.newBuilder().setX(0).setY(0).setZ(0).build();
        return Pose.newBuilder().setRotation(rotation).setTranslation(translation).build();
    }
}
