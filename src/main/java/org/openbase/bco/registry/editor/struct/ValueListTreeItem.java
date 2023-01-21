package org.openbase.bco.registry.editor.struct;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2023 openbase.org
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
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ValueListTreeItem<MB extends Message.Builder> extends AbstractListTreeItem<MB> {

    private DescriptionGenerator descriptionGenerator;
    private EditingGraphicFactory editingGraphicFactory;

    public ValueListTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable) throws InitializationException {
        super(fieldDescriptor, builder, modifiable);
        try {
            validateDescriptor();
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    protected void validateDescriptor() throws CouldNotPerformException {
        super.validateDescriptor();
        if (getFieldDescriptor().getType() == Type.MESSAGE) {
            throw new CouldNotPerformException("FieldDescriptor[" + getFieldDescriptor().getName() + "] of Message[" +
                    extractSimpleMessageClass(getBuilder()) + "] is a message type");
        }
    }

    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() throws CouldNotPerformException {
        ObservableList<TreeItem<ValueType>> childList = FXCollections.observableArrayList();

        for (int i = 0; i < getBuilder().getRepeatedFieldCount(getFieldDescriptor()); i++) {
            childList.add(createChild(getBuilder().getRepeatedField(getFieldDescriptor(), i), i));
        }

        return childList;
    }

    private LeafTreeItem createChild(final Object value, final int index) throws CouldNotPerformException {
        // create leaf tree item
        final LeafTreeItem leafTreeItem = new LeafTreeItem<>(getFieldDescriptor(), value, isModifiable(), index);

        if (editingGraphicFactory != null) {
            leafTreeItem.setEditingGraphicFactory(editingGraphicFactory);
        }

        if (descriptionGenerator != null) {
            leafTreeItem.setDescriptionGenerator(descriptionGenerator);
        }

        // if is modifiable add symbol to remove to child
        updateChildGraphic(leafTreeItem);
        // return tree item
        return leafTreeItem;
    }

    private Object getDefaultValue() throws CouldNotPerformException {
        switch (getFieldDescriptor().getType()) {
            case STRING:
                return "";
            case DOUBLE:
            case FLOAT:
            case INT32:
            case INT64:
                return 0;
            case BOOL:
                return true;
            case ENUM:
                return getFieldDescriptor().getEnumType().getValues().get(0);
            default:
                throw new CouldNotPerformException("Could not generate default element for type[" + getFieldDescriptor().getType().name() + "]");
        }
    }

    @Override
    protected void addElement() throws CouldNotPerformException {
        // generate default value
        final Object defaultValue = getDefaultValue();
        // add to builder
        getBuilder().addRepeatedField(getFieldDescriptor(), defaultValue);
        // add child tree item with this value
        getChildren().add(createChild(defaultValue, getBuilder().getRepeatedFieldCount(getFieldDescriptor()) - 1));
        // expand
        setExpanded(true);
        // trigger update in parent
        setValue(getValueCasted().createNew(getBuilder()));
    }

    @Override
    public void update(final MB value) throws CouldNotPerformException {
        // update internal builder
        setValue(getValueCasted().createNew(value));
        // just create the list anew
        getChildren().clear();
        getChildren().addAll(createChildren());
    }

    public void setDescriptionGenerator(DescriptionGenerator descriptionGenerator) {
        this.descriptionGenerator = descriptionGenerator;
    }

    public void setEditingGraphicFactory(EditingGraphicFactory editingGraphicFactory) {
        this.editingGraphicFactory = editingGraphicFactory;
    }
}
