/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.processing.QuaternionEulerTransform;
import rst.geometry.RotationType.Rotation;

/**
 *
 * @author thuxohl
 */
public class RotationContainer extends NodeContainer<Rotation.Builder> {

    private double[] euler = new double[3];

    public RotationContainer(Rotation.Builder rotation) {
        super("rotation", rotation);
        super.add(rotation.getFrameId(), "frame_id", false);
        euler = QuaternionEulerTransform.transform(rotation.getQw(), rotation.getQx(), rotation.getQy(), rotation.getQz());
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
        euler[0] = Math.toRadians(roll);
        updateQuaternion();
    }

    /**
     * Set the pitch value and update the builder.
     *
     * @param pitch in degree
     */
    public void setPitch(double pitch) {
        euler[1] = Math.toRadians(pitch);
        updateQuaternion();
    }

    /**
     * Set the yaw value and update the builder.
     *
     * @param yaw in degree
     */
    public void setYaw(double yaw) {
        euler[2] = Math.toRadians(yaw);
        updateQuaternion();
    }

    /**
     * Update the rotation builder.
     */
    public void updateQuaternion() {
        double[] quaternion = QuaternionEulerTransform.transformEulerToQuaternion(euler);
        builder.setQw(quaternion[0]);
        builder.setQx(quaternion[1]);
        builder.setQy(quaternion[2]);
        builder.setQz(quaternion[3]);
    }

    /**
     * Get the roll value.
     *
     * @return roll in degree
     */
    public double getRoll() {
        return Math.toDegrees(euler[0]);
    }

    /**
     * Get the pitch value.
     *
     * @return pitch in degree
     */
    public double getPitch() {
        return Math.toDegrees(euler[1]);
    }

    /**
     * Get the yaw value.
     *
     * @return yaw in degree
     */
    public double getYaw() {
        return Math.toDegrees(euler[2]);
    }
}
