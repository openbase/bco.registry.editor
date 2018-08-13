package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.BuilderLeafTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.RotationEditingGraphic;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.processing.QuaternionEulerTransform;
import rst.geometry.RotationType.Rotation.Builder;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import java.text.DecimalFormat;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RotationTreeItem extends BuilderLeafTreeItem<Builder> {

    private final DecimalFormat decimalFormat;

    public RotationTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        decimalFormat = new DecimalFormat("##.##");
    }

    @Override
    protected Node createValueGraphic() {
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

        return new Label("Roll = " + decimalFormat.format(euler.x) + ", Pitch = " + decimalFormat.format(euler.y) + ", Yaw = " + decimalFormat.format(euler.z));
    }

    @Override
    public Node getEditingGraphic(TreeTableCell<ValueType, ValueType> cell) {
        return new RotationEditingGraphic(getValueCasted(), cell).getControl();
    }
}
