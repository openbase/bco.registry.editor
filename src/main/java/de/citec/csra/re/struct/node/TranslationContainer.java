/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.geometry.TranslationType.Translation;

/**
 *
 * @author thuxohl
 */
public class TranslationContainer extends NodeContainer<Translation.Builder> {

    public TranslationContainer(Translation.Builder translation) {
        super("translation", translation);
        super.add(translation.getFrameId(), "frame_id");
        super.add(translation.getX(), "x");
        super.add(translation.getY(), "y");
        super.add(translation.getZ(), "z");
    }

}
