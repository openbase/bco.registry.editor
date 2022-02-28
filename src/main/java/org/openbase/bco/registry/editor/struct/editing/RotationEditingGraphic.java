package org.openbase.bco.registry.editor.struct.editing;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2022 openbase.org
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

import javafx.application.Platform;
import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.HBox;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.NumberFilteredTextField;
import org.openbase.jul.processing.QuaternionEulerTransform;
import org.openbase.type.geometry.RotationType.Rotation.Builder;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import java.text.DecimalFormat;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RotationEditingGraphic extends AbstractBuilderEditingGraphic<HBox, Builder> {

    private NumberFilteredTextField rollTextField, pitchTextField, yawTextField;

    public RotationEditingGraphic(ValueType<Builder> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new HBox(), valueType, treeTableCell);
    }

    @Override
    protected boolean updateBuilder(Builder builder) {
        double roll = Math.toRadians(rollTextField.getAsDouble());
        double pitch = Math.toRadians(pitchTextField.getAsDouble());
        double yaw = Math.toRadians(yawTextField.getAsDouble());
        final Vector3d euler = new Vector3d(roll, pitch, yaw);

        Quat4d quaternion = QuaternionEulerTransform.transform(euler);
        if (quaternion.w == builder.getQw() && quaternion.x == builder.getQx() && quaternion.y == builder.getQy() && quaternion.z == builder.getQz()) {
            return false;
        }

        builder.setQw(quaternion.w);
        builder.setQx(quaternion.x);
        builder.setQy(quaternion.y);
        builder.setQz(quaternion.z);
        return true;
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

        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        rollTextField.setText("" + decimalFormat.format(euler.x));
        pitchTextField.setText("" + decimalFormat.format(euler.y));
        yawTextField.setText("" + decimalFormat.format(euler.z));

        Platform.runLater(() -> rollTextField.requestFocus());
    }
}
