package org.openbase.bco.registry.editor.util;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
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
import org.openbase.jul.extension.rst.processing.TimestampProcessor;
import rst.domotic.state.ActivationStateType.ActivationState;
import rst.domotic.state.EnablingStateType.EnablingState;
import rst.domotic.state.InventoryStateType.InventoryState;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import rst.domotic.unit.agent.AgentConfigType.AgentConfig;
import rst.domotic.unit.app.AppConfigType.AppConfig;
import rst.domotic.unit.authorizationgroup.AuthorizationGroupConfigType.AuthorizationGroupConfig;
import rst.domotic.unit.connection.ConnectionConfigType.ConnectionConfig;
import rst.domotic.unit.device.DeviceClassType.DeviceClass;
import rst.domotic.unit.device.DeviceConfigType.DeviceConfig;
import rst.domotic.unit.location.LocationConfigType.LocationConfig;
import rst.domotic.unit.scene.SceneConfigType.SceneConfig;
import rst.domotic.unit.unitgroup.UnitGroupConfigType.UnitGroupConfig;
import rst.domotic.unit.user.UserConfigType.UserConfig;
import rst.geometry.AxisAlignedBoundingBox3DFloatType.AxisAlignedBoundingBox3DFloat;
import rst.geometry.PoseType.Pose;
import rst.geometry.RotationType.Rotation;
import rst.geometry.TranslationType.Translation;
import rst.math.Vec3DDoubleType.Vec3DDouble;
import rst.spatial.PlacementConfigType.PlacementConfig;
import rst.spatial.ShapeType.Shape;
import rst.timing.TimestampType.Timestamp;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RSTDefaultInstances {

    public static Timestamp getDefaultTimestamp() {
        return TimestampProcessor.getCurrentTimestamp();
    }

    public static InventoryState getDefaultInventoryState() {
        return InventoryState.newBuilder().setTimestamp(getDefaultTimestamp()).build();
    }

    public static DeviceConfig.Builder getDefaultDeviceConfig() {
        return DeviceConfig.newBuilder();
    }

    public static Translation getDefaultTranslation() {
        return Translation.newBuilder().setX(0).setY(0).setZ(0).build();
    }

    public static Rotation getDefaultRotation() {
        return Rotation.newBuilder().setQw(1).setQx(0).setQy(0).setQz(0).build();
    }

    public static AxisAlignedBoundingBox3DFloat getDefaultBoundingBox() {
        return AxisAlignedBoundingBox3DFloat.newBuilder().setDepth(1).setHeight(1).setWidth(1).setLeftFrontBottom(getDefaultTranslation()).build();
    }

    public static Shape getDefaultShape() {
        return Shape.newBuilder().setBoundingBox(getDefaultBoundingBox()).build();
    }

    public static Pose getDefaultPose() {
        return Pose.newBuilder().setRotation(getDefaultRotation()).setTranslation(getDefaultTranslation()).build();
    }

    public static LocationConfig.Builder getDefaultLocationConfig() {
        return LocationConfig.newBuilder();
    }

    public static ConnectionConfig.Builder getDefaultConnectionConfig() {
        return ConnectionConfig.newBuilder();
    }

    public static ActivationState getDefaultActivationState() {
        return ActivationState.newBuilder().setValue(ActivationState.State.ACTIVE).build();
    }

    public static EnablingState getDefaultEnablingState() {
        return EnablingState.newBuilder().setValue(EnablingState.State.ENABLED).setTimestamp(getDefaultTimestamp()).build();
    }

    public static SceneConfig.Builder getDefaultSceneConfig() {
        return SceneConfig.newBuilder();
    }

    public static AgentConfig.Builder getDefaultAgentConfig() {
        return AgentConfig.newBuilder();
    }

    public static AppConfig.Builder getDefaultAppConfig() {
        return AppConfig.newBuilder();
    }

    public static UserConfig.Builder getDefaultUserConfig() {
        return UserConfig.newBuilder();
    }

    public static AuthorizationGroupConfig.Builder getDefaultAuthorizationGroupConfig() {
        return AuthorizationGroupConfig.newBuilder();
    }

    public static PlacementConfig.Builder getDefaultPlacementConfig() {
        return PlacementConfig.newBuilder().setPosition(getDefaultPose()).setShape(getDefaultShape());
    }

    public static Vec3DDouble.Builder getDefaultVec3DDouble() {
        return Vec3DDouble.newBuilder().setX(0).setY(0).setZ(0);
    }

    public static UnitGroupConfig.Builder getDefaultUnitGroupConfig() {
        return UnitGroupConfig.newBuilder();
    }

    public static DeviceClass.Builder getDefaultDeviceClass() {
        return DeviceClass.newBuilder().setShape(getDefaultShape());
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
        } else if (builderType instanceof UserConfig.Builder) {
            return getDefaultUserConfig();
        } else if (builderType instanceof AuthorizationGroupConfig.Builder) {
            return getDefaultAuthorizationGroupConfig();
        } else if (builderType instanceof ConnectionConfig.Builder) {
            return getDefaultConnectionConfig();
        } else if (builderType instanceof UnitGroupConfig.Builder) {
            return getDefaultUnitGroupConfig();
        } else if (builderType instanceof DeviceClass.Builder) {
            return getDefaultDeviceClass();
        } else if (builderType instanceof UnitConfig.Builder) {
            switch (((UnitConfig.Builder) builderType).getUnitType()) {
                case AUTHORIZATION_GROUP:
                    return UnitConfig.getDefaultInstance().toBuilder().setUnitType(UnitType.AUTHORIZATION_GROUP);
                case AGENT:
                    return UnitConfig.getDefaultInstance().toBuilder().setUnitType(UnitType.AGENT);
                case APP:
                    return UnitConfig.getDefaultInstance().toBuilder().setUnitType(UnitType.APP);
                case CONNECTION:
                    return UnitConfig.getDefaultInstance().toBuilder().setUnitType(UnitType.CONNECTION);
                case DEVICE:
                    return UnitConfig.getDefaultInstance().toBuilder().setUnitType(UnitType.DEVICE);
                case LOCATION:
                    return UnitConfig.getDefaultInstance().toBuilder().setUnitType(UnitType.LOCATION);
                case SCENE:
                    return UnitConfig.getDefaultInstance().toBuilder().setUnitType(UnitType.SCENE);
                case UNIT_GROUP:
                    return UnitConfig.getDefaultInstance().toBuilder().setUnitType(UnitType.UNIT_GROUP);
                case USER:
                    return UnitConfig.getDefaultInstance().toBuilder().setUnitType(UnitType.USER);
                default:
                    return UnitConfig.getDefaultInstance().toBuilder();
            }
        } else {
            return (Builder) builderType.build().getDefaultInstanceForType().toBuilder();
        }
    }
}
