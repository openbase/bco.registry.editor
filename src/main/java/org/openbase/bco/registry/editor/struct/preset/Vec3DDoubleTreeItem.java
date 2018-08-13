package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openbase.bco.registry.editor.struct.BuilderLeafTreeItem;
import org.openbase.jul.exception.InitializationException;
import rst.math.Vec3DDoubleType.Vec3DDouble;
import rst.math.Vec3DDoubleType.Vec3DDouble.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class Vec3DDoubleTreeItem extends BuilderLeafTreeItem<Vec3DDouble.Builder> {

    public Vec3DDoubleTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @Override
    protected Node createDescriptionGraphic() {
        return new Label("x = " + getBuilder().getX() + ", y = " + getBuilder().getY() + ", z = " + getBuilder().getZ());
    }
}
