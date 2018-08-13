package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.HBox;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.NumberFilteredTextField;
import rst.math.Vec3DDoubleType.Vec3DDouble;
import rst.math.Vec3DDoubleType.Vec3DDouble.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class Vec3DDoubleEditingGraphic extends AbstractBuilderEditingGraphic<HBox, Vec3DDouble.Builder> {

    private NumberFilteredTextField xTextField, yTextField, zTextField;

    Vec3DDoubleEditingGraphic(HBox control, ValueType<Builder> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new HBox(), valueType, treeTableCell);
    }

    @Override
    protected void updateBuilder(final Builder builder) {
        builder.setX(xTextField.getAsDouble());
        builder.setY(yTextField.getAsDouble());
        builder.setZ(zTextField.getAsDouble());
    }

    @Override
    protected boolean validate() {
        return xTextField.validate() && yTextField.validate() && zTextField.validate();
    }

    @Override
    protected void init(final Builder value) {
        xTextField = new NumberFilteredTextField();
        yTextField = new NumberFilteredTextField();
        zTextField = new NumberFilteredTextField();

        getControl().getChildren().addAll(xTextField, yTextField, zTextField);

        xTextField.setText(value.getX() + "");
        yTextField.setText(value.getY() + "");
        zTextField.setText(value.getZ() + "");
    }
}
