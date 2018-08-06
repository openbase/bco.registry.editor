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
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ValueListTreeItem<MB extends Message.Builder> extends AbstractBuilderTreeItem<MB> {

    private final boolean modifiable;

    public ValueListTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable) throws InitializationException {
        super(fieldDescriptor, builder);

        try {
            validateDescriptor();
            this.modifiable = modifiable;
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    private void validateDescriptor() throws CouldNotPerformException {
        if (getFieldDescriptor().getType() == Type.MESSAGE || !getFieldDescriptor().isRepeated()) {
            throw new CouldNotPerformException("FieldDescriptor[" + getFieldDescriptor() + "] of Message[" + extractMessageClass(getBuilder()) + "] is not a repeated message");
        }
    }

    public boolean isModifiable() {
        return modifiable;
    }

    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() {
        ObservableList<TreeItem<ValueType>> childList = FXCollections.observableArrayList();

        for (int i = 0; i < getBuilder().getRepeatedFieldCount(getFieldDescriptor()); i++) {
            //TODO: something else than genericTreeItem
            try {
                childList.add(new GenericTreeItem<>(getFieldDescriptor(), getBuilder().getRepeatedField(getFieldDescriptor(), i)));
            } catch (InitializationException ex) {
                logger.error("Could not generate child for [" + getBuilder().getClass().getName() + "]");
            }
        }

        return childList;
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

    public void addElement() throws CouldNotPerformException {
        // generate default value
        final Object defaultValue = getDefaultValue();
        // add to builder
        getBuilder().addRepeatedField(getFieldDescriptor(), defaultValue);
        // add child tree item with this value
        //TODO: child has to now its index
        getChildren().add(new GenericTreeItem<>(getFieldDescriptor(), getDefaultValue()));
        // expand
        setExpanded(true);
    }
}
