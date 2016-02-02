package org.dc.bco.registry.editor.util;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 DivineCooperation
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.protobuf.GeneratedMessage.Builder;
import java.util.Date;
import rst.geometry.PoseType.Pose;
import rst.geometry.RotationType.Rotation;
import rst.geometry.TranslationType.Translation;
import rst.homeautomation.control.agent.AgentConfigType.AgentConfig;
import rst.homeautomation.control.app.AppConfigType.AppConfig;
import rst.homeautomation.control.scene.SceneConfigType.SceneConfig;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.state.ActivationStateType.ActivationState;
import rst.homeautomation.state.EnablingStateType.EnablingState;
import rst.homeautomation.state.InventoryStateType.InventoryState;
import rst.homeautomation.unit.UnitConfigType.UnitConfig;
import rst.math.Vec3DDoubleType.Vec3DDouble;
import rst.spatial.ConnectionConfigType.ConnectionConfig;
import rst.spatial.LocationConfigType.LocationConfig;
import rst.spatial.PlacementConfigType.PlacementConfig;
import rst.spatial.ShapeType;
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

    public static Pose getDefaultPose() {
        Rotation rotation = Rotation.newBuilder().setQw(1).setQx(0).setQy(0).setQz(0).build();
        Translation translation = Translation.newBuilder().setX(0).setY(0).setZ(0).build();
        return Pose.newBuilder().setRotation(rotation).setTranslation(translation).build();
    }

    public static LocationConfig.Builder getDefaultLocationConfig() {
        return LocationConfig.newBuilder().setPlacementConfig(PlacementConfig.newBuilder().setPosition(getDefaultPose()));
    }

    public static ConnectionConfig.Builder getDefaultConnectionConfig() {
        return ConnectionConfig.newBuilder().setPlacementConfig(PlacementConfig.newBuilder().setPosition(getDefaultPose()));
    }

    public static ShapeType.Shape getDefaultShape() {
        return null;
    }

    public static ActivationState getDefaultActivationState() {
        return ActivationState.newBuilder().setValue(ActivationState.State.ACTIVE).build();
    }

    public static EnablingState getDefaultEnablingState() {
        return EnablingState.newBuilder().setValue(EnablingState.State.ENABLED).build();
    }

    public static SceneConfig.Builder getDefaultSceneConfig() {
        return SceneConfig.newBuilder().setActivationState(getDefaultActivationState());
    }

    public static AgentConfig.Builder getDefaultAgentConfig() {
        return AgentConfig.newBuilder().setEnablingState(getDefaultEnablingState());
    }

    public static AppConfig.Builder getDefaultAppConfig() {
        return AppConfig.newBuilder().setActivationState(getDefaultActivationState());
    }

    public static Vec3DDouble.Builder getDefaultVec3DDouble() {
        return Vec3DDouble.newBuilder().setX(0).setY(0).setZ(0);
    }

    public static Builder getDefaultBuilder(Builder builderType) {
        if (builderType instanceof DeviceConfig.Builder) {
            return getDefaultDeviceConfig();
        } else if (builderType instanceof LocationConfig.Builder) {
            return getDefaultLocationConfig();
        } else if (builderType instanceof SceneConfig.Builder) {
            return getDefaultSceneConfig();
        } else if (builderType instanceof AgentConfig.Builder) {
            return getDefaultAgentConfig();
        } else if (builderType instanceof AppConfig.Builder) {
            return getDefaultAppConfig();
        } else if (builderType instanceof Vec3DDouble.Builder) {
            return getDefaultVec3DDouble();
        } else {
            return (Builder) builderType.build().getDefaultInstanceForType().toBuilder();
        }
    }
}
