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

import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.HBox;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.NumberFilteredTextField;
import org.openbase.type.domotic.state.GlobalPositionStateType.GlobalPositionState;
import org.openbase.type.domotic.state.GlobalPositionStateType.GlobalPositionState.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class GlobalPositionStateEditingGraphic extends AbstractBuilderEditingGraphic<HBox, GlobalPositionState.Builder> {

    private NumberFilteredTextField longitudeTextField, latitudeTextField, elevationTextField;

    public GlobalPositionStateEditingGraphic(ValueType<Builder> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new HBox(), valueType, treeTableCell);
    }

    @Override
    protected boolean updateBuilder(final Builder builder) {
        final double newLongitude = longitudeTextField.getAsDouble();
        final double newLatitude = latitudeTextField.getAsDouble();
        final double newElevation = elevationTextField.getAsDouble();

        if (newLongitude == builder.getLongitude() && newLatitude == builder.getLatitude() && newElevation == builder.getElevation()) {
            return false;
        }

        builder.setLongitude(newLongitude).setLatitude(newLatitude).setElevation(newElevation);
        return true;
    }

    @Override
    protected boolean validate() {
        return longitudeTextField.validate() && latitudeTextField.validate() && elevationTextField.validate();
    }

    @Override
    protected void init(final Builder value) {
        longitudeTextField = new NumberFilteredTextField();
        latitudeTextField = new NumberFilteredTextField();
        elevationTextField = new NumberFilteredTextField();

        getControl().getChildren().addAll(longitudeTextField, latitudeTextField, elevationTextField);

        longitudeTextField.setText(value.getLongitude() + "");
        latitudeTextField.setText(value.getLatitude() + "");
        elevationTextField.setText(value.getElevation() + "");
    }
}
