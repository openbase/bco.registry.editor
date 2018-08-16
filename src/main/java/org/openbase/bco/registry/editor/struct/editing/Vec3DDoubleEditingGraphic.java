package org.openbase.bco.registry.editor.struct.editing;

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

    public Vec3DDoubleEditingGraphic(ValueType<Builder> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new HBox(), valueType, treeTableCell);
    }

    @Override
    protected boolean updateBuilder(final Builder builder) {
        final double newX = xTextField.getAsDouble();
        final double newY = yTextField.getAsDouble();
        final double newZ = zTextField.getAsDouble();

        if (newX == builder.getX() && newY == builder.getY() && newZ == builder.getZ()) {
            return false;
        }

        builder.setX(newX).setY(newY).setZ(newZ);
        return true;
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
