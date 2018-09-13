package org.openbase.bco.registry.editor.struct.editing.service;

/*-
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

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import org.openbase.bco.registry.editor.struct.editing.ServiceStateAttributeEditingGraphic;
import rst.domotic.state.ColorStateType.ColorState;
import rst.vision.ColorType;
import rst.vision.ColorType.Color.Type;
import rst.vision.HSBColorType.HSBColor;
import rst.vision.RGBColorType.RGBColor;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ColorStateEditingGraphic extends AbstractServiceStateEditingGraphic<ColorState, ColorPicker> {

    public ColorStateEditingGraphic() {
        super(new ColorPicker(), ColorState.class);
    }

    @Override
    void internalInit(ColorState colorState) {
        Color color = Color.WHITE;
        switch (colorState.getColor().getType()) {
            case HSB:
                final HSBColor hsbColor = colorState.getColor().getHsbColor();
                color = Color.hsb(hsbColor.getHue(), hsbColor.getSaturation(), hsbColor.getBrightness());
                break;
            case RGB:
                final RGBColor rgbColor = colorState.getColor().getRgbColor();
                color = Color.rgb(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue());
                break;

        }
        getGraphic().setValue(color);
    }

    @Override
    public ColorState getServiceState() {
        final Color color = getGraphic().getValue();

        final ColorState.Builder colorState = ColorState.newBuilder();
        final ColorType.Color.Builder colorBuilder = colorState.getColorBuilder();
        colorBuilder.setType(Type.HSB);

        final HSBColor.Builder hsbColorBuilder = colorBuilder.getHsbColorBuilder();
        hsbColorBuilder.setHue(color.getHue()).setSaturation(color.getSaturation()).setBrightness(color.getBrightness());
        return colorState.build();
    }

    @Override
    public void addCommitEditEventHandler(final ServiceStateAttributeEditingGraphic editingGraphic) {
        getGraphic().setOnAction(event -> editingGraphic.commitEdit());
    }
}
