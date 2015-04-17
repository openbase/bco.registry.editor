/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.geometry.RotationType.Rotation;

/**
 *
 * @author thuxohl
 */
public class RotationContainer extends NodeContainer<Rotation.Builder> {

    public RotationContainer(Rotation.Builder rotation) {
        super("rotation", rotation);
        super.add(rotation.getFrameId(), "frame_id");
        super.add(rotation.getQw(), "qw");
        super.add(rotation.getQx(), "qx");
        super.add(rotation.getQy(), "qy");
        super.add(rotation.getQz(), "qz");
    }
    
}
