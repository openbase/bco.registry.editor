package org.openbase.bco.registry.editor.struct;

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

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.editing.*;
import org.openbase.bco.registry.editor.util.DefaultDescriptionGenerator;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.bco.registry.editor.util.SelectableLabel;
import org.openbase.jul.exception.CouldNotPerformException;

import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LeafTreeItem<V> extends GenericTreeItem<V> {

    private DescriptionGenerator<V> descriptionGenerator;
    private EditingGraphicFactory<V, ?> editingGraphicFactory;

    public LeafTreeItem(final FieldDescriptor fieldDescriptor, final V value, final Message.Builder parentBuilder, final int index) {
        this(fieldDescriptor, value, parentBuilder, true, index);
    }

    public LeafTreeItem(final FieldDescriptor fieldDescriptor, final V value, final Message.Builder parentBuilder, final boolean editable) {
        this(fieldDescriptor, value, parentBuilder, editable, -1);
    }

    public LeafTreeItem(final FieldDescriptor fieldDescriptor, final V value, final Message.Builder parentBuilder, final boolean editable, final int index) {
        super(fieldDescriptor, value, editable);
        this.editingGraphicFactory = null;
        this.descriptionGenerator = new DefaultDescriptionGenerator<>();
        this.addEventHandler(valueChangedEvent(), event -> {
            V newValue = ((ValueType<V>) event.getNewValue()).getValue();
            if (index == -1) {
                parentBuilder.setField(getFieldDescriptor(), newValue);
            } else {
                parentBuilder.setRepeatedField(getFieldDescriptor(), index, newValue);
            }
            updateValueGraphic();
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public Node getEditingGraphic(final TreeTableCell<ValueType, ValueType> cell) {
        if (editingGraphicFactory != null) {
            try {
                return editingGraphicFactory.getEditingGraphic(getValueCasted(), cell);
            } catch (CouldNotPerformException ex) {
                logger.warn("Could not create editing graphic", ex);
            }
        }
        switch (getFieldDescriptor().getType()) {
            case BOOL:
                return new BooleanEditingGraphic(getValue(), cell).getControl();
            case ENUM:
                return new EnumEditingGraphic(getValue(), cell).getControl();
            case FLOAT:
                return new FloatEditingGraphic(getValue(), cell).getControl();
            case DOUBLE:
                return new DoubleEditingGraphic(getValue(), cell).getControl();
            case STRING:
                return new StringEditingGraphic(getValue(), cell).getControl();
            default:
                logger.warn("Editing not supported for field with type[" + getFieldDescriptor().getType().name() + "]");
                return null;
        }
    }

    @Override
    protected Node createValueGraphic() {
        if (isEditable()) {
            return new Label(descriptionGenerator.getDescription(getInternalValue()));
        } else {
            return SelectableLabel.makeSelectable(new Label(descriptionGenerator.getDescription(getInternalValue())));
        }
    }

    public void setEditingGraphicFactory(EditingGraphicFactory<V, ?> editingGraphicFactory) {
        this.editingGraphicFactory = editingGraphicFactory;
    }

    public void setDescriptionGenerator(final DescriptionGenerator<V> descriptionGenerator) {
        this.descriptionGenerator = descriptionGenerator;
    }

    @Override
    public void search(final String text, final List<GenericTreeItem> treeItemList) {
        if (descriptionGenerator.getDescription(getInternalValue()).contains(text)) {
            treeItemList.add(this);
        }
    }
}
