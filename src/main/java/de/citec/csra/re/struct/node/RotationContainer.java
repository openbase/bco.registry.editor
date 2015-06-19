/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.processing.QuaternionEulerTransform;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import rst.geometry.RotationType.Rotation;

/**
 *
 * @author thuxohl
 */
public class RotationContainer extends NodeContainer<Rotation.Builder> {

    private final Vector3d euler;

    public RotationContainer(Rotation.Builder rotation) {
        super("rotation", rotation);
        super.add(rotation.getFrameId(), "frame_id", false);
        euler = QuaternionEulerTransform.transform(new Quat4d(rotation.getQw(), rotation.getQx(), rotation.getQy(), rotation.getQz()));
        super.add((double) Math.round(getRoll()), "roll");
        super.add((double) Math.round(getPitch()), "pitch");
        super.add((double) Math.round(getYaw()), "yaw");
    }

    /**
     * Set the roll value and update the builder.
     *
     * @param roll in degree
     */
    public void setRoll(double roll) {
        euler.x = Math.toRadians(roll);
        updateQuaternion();
    }

    /**
     * Set the pitch value and update the builder.
     *
     * @param pitch in degree
     */
    public void setPitch(double pitch) {
        euler.y = Math.toRadians(pitch);
        updateQuaternion();
    }

    /**
     * Set the yaw value and update the builder.
     *
     * @param yaw in degree
     */
    public void setYaw(double yaw) {
        euler.y = Math.toRadians(yaw);
        updateQuaternion();
    }

    /**
     * Update the rotation builder.
     */
    public void updateQuaternion() {
        Quat4d quaternion = QuaternionEulerTransform.transform(euler);
        builder.setQw(quaternion.w);
        builder.setQx(quaternion.x);
        builder.setQy(quaternion.y);
        builder.setQz(quaternion.z);
    }

    /**
     * Get the roll value.
     *
     * @return roll in degree
     */
    public double getRoll() {
        return Math.toDegrees(euler.x);
    }

    /**
     * Get the pitch value.
     *
     * @return pitch in degree
     */
    public double getPitch() {
        return Math.toDegrees(euler.y);
    }

    /**
     * Get the yaw value.
     *
     * @return yaw in degree
     */
    public double getYaw() {
        return Math.toDegrees(euler.z);
    }
}
