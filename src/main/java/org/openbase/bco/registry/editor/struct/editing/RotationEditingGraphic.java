package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.HBox;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.NumberFilteredTextField;
import org.openbase.jul.processing.QuaternionEulerTransform;
import rst.geometry.RotationType.Rotation.Builder;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import java.text.DecimalFormat;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RotationEditingGraphic extends AbstractBuilderEditingGraphic<HBox, Builder> {


    private NumberFilteredTextField rollTextField, pitchTextField, yawTextField;
    private DecimalFormat decimalFormat;

    public RotationEditingGraphic(ValueType<Builder> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new HBox(), valueType, treeTableCell);

        getControl().setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ENTER:
                    commitEdit();
                    break;
                case ESCAPE:
                    treeTableCell.cancelEdit();
                    break;
            }
        });
    }

    @Override
    protected void updateBuilder(Builder builder) {
        double roll = Math.toRadians(rollTextField.getAsDouble());
        double pitch = Math.toRadians(pitchTextField.getAsDouble());
        double yaw = Math.toRadians(yawTextField.getAsDouble());
        final Vector3d euler = new Vector3d(roll, pitch, yaw);

        Quat4d quaternion = QuaternionEulerTransform.transform(euler);
        builder.setQw(quaternion.w);
        builder.setQx(quaternion.x);
        builder.setQy(quaternion.y);
        builder.setQz(quaternion.z);
    }

    @Override
    protected boolean validate() {
        return rollTextField.validate() && pitchTextField.validate() && yawTextField.validate();
    }

    @Override
    protected void init(Builder value) {
        this.rollTextField = new NumberFilteredTextField();
        this.pitchTextField = new NumberFilteredTextField();
        this.yawTextField = new NumberFilteredTextField();

        getControl().getChildren().addAll(rollTextField, pitchTextField, yawTextField);

        final Vector3d euler;
        /* Even though the rotation type has defined default values they do not seem to be used.
         * Thus assume the default rotation as none.
         * Tts enough to check if only qw is not set because these fields are required.
         * So if one is not set then every other value is not set either.
         */
        if (!value.hasQw()) {
            euler = new Vector3d(0.0, 0.0, 0.0);
        } else {
            euler = QuaternionEulerTransform.transform(new Quat4d(value.getQx(), value.getQy(), value.getQz(), value.getQw()));
            euler.x = Math.toDegrees(euler.x);
            euler.y = Math.toDegrees(euler.y);
            euler.z = Math.toDegrees(euler.z);
        }

        decimalFormat = new DecimalFormat("##.##");
        rollTextField.setText("" + decimalFormat.format(euler.x));
        pitchTextField.setText("" + decimalFormat.format(euler.y));
        yawTextField.setText("" + decimalFormat.format(euler.z));
    }
}
