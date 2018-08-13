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

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.AbstractBuilderTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.EnumValueDescriptorCell;
import rst.domotic.state.EnablingStateType.EnablingState;
import rst.domotic.state.EnablingStateType.EnablingState.Builder;
import rst.domotic.state.EnablingStateType.EnablingState.State;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class EnablingStateEditingGraphic extends AbstractBuilderEditingGraphic<ComboBox<EnumValueDescriptor>, Builder> {

    public EnablingStateEditingGraphic(final ValueType<EnablingState.Builder> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new ComboBox<>(), valueType, treeTableCell);
        getControl().setVisibleRowCount(10);
        getControl().setCellFactory(param -> new EnumValueDescriptorCell());
        getControl().setButtonCell(new EnumValueDescriptorCell());
        getControl().setOnAction((event) -> commitEdit());
    }

    @Override
    protected void commitEdit() {
        if (getControl().getSelectionModel().getSelectedItem() != null) {
            super.commitEdit();
        }
    }
    @Override
    protected void updateBuilder(Builder builder) {
        builder.setValue(State.valueOf(getControl().getSelectionModel().getSelectedItem().getNumber()));
    }

    @Override
    protected void init(final EnablingState.Builder value) {
        getControl().setItems(EnumEditingGraphic.createSortedEnumList(value.getValue().getDescriptorForType()));
        getControl().setValue(value.getValue().getValueDescriptor());
    }
}
