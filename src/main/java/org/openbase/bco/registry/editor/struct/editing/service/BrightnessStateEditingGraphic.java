package org.openbase.bco.registry.editor.struct.editing.service;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import rst.domotic.state.BrightnessStateType.BrightnessState;

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
        this.slider = new Slider(0, 100, 0);
        this.slider.setShowTickLabels(true);
        this.slider.setShowTickMarks(true);
        this.slider.setBlockIncrement(10);
        this.label = new Label(decimalFormat.format(slider.getValue()));

        this.slider.valueProperty().addListener((observable, oldValue, newValue) -> label.setText(decimalFormat.format(newValue.doubleValue())));

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
