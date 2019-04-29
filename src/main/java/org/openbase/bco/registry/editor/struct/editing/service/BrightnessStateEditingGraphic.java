package org.openbase.bco.registry.editor.struct.editing.service;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2019 openbase.org
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

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import org.openbase.type.domotic.state.BrightnessStateType.BrightnessState;

import java.text.DecimalFormat;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class BrightnessStateEditingGraphic extends AbstractServiceStateEditingGraphic<BrightnessState, HBox> {

    private final Slider slider;
    private final Label label;
    private final DecimalFormat decimalFormat;

    public BrightnessStateEditingGraphic() {
        super(new HBox(), BrightnessState.class);

        this.decimalFormat = new DecimalFormat("##.##");
        this.slider = new Slider(0, 1, 0);
        this.slider.setShowTickLabels(true);
        this.slider.setShowTickMarks(true);
        this.slider.setBlockIncrement(0.1);
        this.label = new Label(decimalFormat.format(slider.getValue()));

        this.slider.valueProperty().addListener((observable, oldValue, newValue) -> label.setText(decimalFormat.format(newValue.doubleValue() * 100)));

        getGraphic().setSpacing(10);
        getGraphic().getChildren().addAll(slider, label);
    }

    @Override
    void internalInit(final BrightnessState brightnessState) {
        slider.setValue(brightnessState.getBrightness());
    }

    @Override
    public BrightnessState getServiceState() {
        return BrightnessState.newBuilder().setBrightness(slider.getValue()).build();
    }
}
