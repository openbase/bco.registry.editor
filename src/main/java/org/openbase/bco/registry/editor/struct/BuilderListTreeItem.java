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
import com.google.protobuf.Message.Builder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.protobuf.BuilderProcessor;

import java.util.List;

import static com.google.protobuf.Descriptors.FieldDescriptor.Type.MESSAGE;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class BuilderListTreeItem<MB extends Message.Builder> extends AbstractBuilderTreeItem<MB> {

    private final boolean modifiable;
    private final List<Message.Builder> builderList;

    public BuilderListTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable) throws InitializationException {
        super(fieldDescriptor, builder);

        try {
            validateDescriptor();
            this.modifiable = modifiable;
            this.builderList = BuilderProcessor.extractRepeatedFieldBuilderList(fieldDescriptor, builder);
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    public BuilderListTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable, final List<Message.Builder> builderList) throws InitializationException {
        super(fieldDescriptor, builder);
        try {
            validateDescriptor();
            this.modifiable = modifiable;
            this.builderList = builderList;
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    private void validateDescriptor() throws CouldNotPerformException {
        if (getFieldDescriptor().getType() != MESSAGE || !getFieldDescriptor().isRepeated()) {
            throw new CouldNotPerformException("FieldDescriptor[" + getFieldDescriptor() + "] of Message[" + extractMessageClass(getBuilder()) + "] is not a repeated message");
        }
    }

    public boolean isModifiable() {
        return modifiable;
    }

    public List<Builder> getBuilderList() {
        return builderList;
    }

    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() throws CouldNotPerformException {
        ObservableList<TreeItem<ValueType>> childList = FXCollections.observableArrayList();

        for (final Message.Builder builder : builderList) {
            childList.add(loadTreeItem(getFieldDescriptor(), builder));
        }
//            for (int i = 0; i < getBuilder().getRepeatedFieldCount(fieldDescriptor); i++) {
//                childList.add(new GenericTreeItem<>(fieldDescriptor, getBuilder().getRepeatedField(fieldDescriptor, i)));
//            }

        return childList;
    }

    public void addElement() throws CouldNotPerformException {
//        try {
            // add the new message builder to the repeated field
//            final Message.Builder builder = BuilderProcessor.addDefaultInstanceToRepeatedField(fieldDescriptor, getBuilder());
            // if available set value from parent group
//            if (getParent() instanceof GroupTreeItem) {
//                ((GroupTreeItem) getParent()).updateElement(builder);
//            }
//            // create tree item and add it
//            getChildren().add(loadTreeItem(fieldDescriptor, builder));
//            getChildren().get(this.getChildren().size() - 1).setExpanded(true);
//            // expand new element
//            setExpanded(true);
//        } catch (CouldNotPerformException ex) {
//            throw new CouldNotPerformException("Could not new element for [" + fieldDescriptor.getName() + "] of Message[" + extractMessageClass(getBuilder()) + "]!", ex);
//        }
    }
}
