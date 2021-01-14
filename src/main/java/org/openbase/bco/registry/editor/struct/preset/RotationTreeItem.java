package org.openbase.bco.registry.editor.struct.preset;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2021 openbase.org
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

import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.scene.Node;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.AbstractBuilderLeafTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.RotationEditingGraphic;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.processing.QuaternionEulerTransform;
import org.openbase.type.geometry.RotationType.Rotation.Builder;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import java.text.DecimalFormat;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RotationTreeItem extends AbstractBuilderLeafTreeItem<Builder> {

    private final DecimalFormat decimalFormat;

    public RotationTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        decimalFormat = new DecimalFormat("##.##");
    }

    @Override
    protected String createValueRepresentation() {
        final Vector3d euler;
        /* Even though the rotation type has defined default values they do not seem to be used.
         * Thus assume the default rotation as none.
         * Tts enough to check if only qw is not set because these fields are required.
         * So if one is not set then every other value is not set either.
         */
        if (!getBuilder().hasQw()) {
            euler = new Vector3d(0.0, 0.0, 0.0);
        } else {
            euler = QuaternionEulerTransform.transform(new Quat4d(getBuilder().getQx(), getBuilder().getQy(), getBuilder().getQz(), getBuilder().getQw()));
            euler.x = Math.toDegrees(euler.x);
            euler.y = Math.toDegrees(euler.y);
            euler.z = Math.toDegrees(euler.z);
        }

        return "Roll = " + decimalFormat.format(euler.x) + ", Pitch = " + decimalFormat.format(euler.y) + ", Yaw = " + decimalFormat.format(euler.z);
    }

    @Override
    public Node getEditingGraphic(TreeTableCell<ValueType, ValueType> cell) {
        return new RotationEditingGraphic(getValueCasted(), cell).getControl();
    }
}
