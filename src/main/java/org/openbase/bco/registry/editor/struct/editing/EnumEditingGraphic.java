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

import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.EnumValueDescriptorCell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class EnumEditingGraphic extends AbstractEditingGraphic<ComboBox<EnumValueDescriptor>, EnumValueDescriptor> {

    private static final String ENUM_NAME_UNKNOWN = "UNKNOWN";

    public EnumEditingGraphic(final ValueType<EnumValueDescriptor> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
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
    protected EnumValueDescriptor getCurrentValue() {
        return getControl().getSelectionModel().getSelectedItem();
    }

    @Override
    protected void init(final EnumValueDescriptor value) {
        getControl().setItems(createSortedEnumList(value.getType()));
        getControl().setValue(value);
    }

    static ObservableList<EnumValueDescriptor> createSortedEnumList(EnumDescriptor enumDescriptor) {
        List<EnumValueDescriptor> values = new ArrayList<>();
        for (EnumValueDescriptor value : enumDescriptor.getValues()) {
            if (value.getName().equals(ENUM_NAME_UNKNOWN)) {
                continue;
            }

            values.add(value);
        }
        values.sort(Comparator.comparing(EnumValueDescriptor::getName));
        return FXCollections.observableArrayList(values);
    }
}
