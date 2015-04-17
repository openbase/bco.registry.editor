/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.geometry.PoseType.Pose;

/**
 *
 * @author thuxohl
 */
public class PositionContainer extends NodeContainer<Pose.Builder> {

    public PositionContainer(Pose.Builder position) {
        super("Position", position);
        super.add(new TranslationContainer(position.getTranslationBuilder()));
        super.add(new RotationContainer(position.getRotationBuilder()));
    }
}
