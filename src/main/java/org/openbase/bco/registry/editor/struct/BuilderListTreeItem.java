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

import java.util.ArrayList;
import java.util.List;

import static com.google.protobuf.Descriptors.FieldDescriptor.Type.MESSAGE;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class BuilderListTreeItem<MB extends Message.Builder> extends AbstractListTreeItem<MB> {

    private List<Message.Builder> builderList;

    public BuilderListTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable) throws InitializationException {
        this(fieldDescriptor, builder, modifiable, null);
        try {
            setBuilderList(BuilderProcessor.extractRepeatedFieldBuilderList(fieldDescriptor, builder));
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    BuilderListTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable, final List<Message.Builder> builderList) throws InitializationException {
        super(fieldDescriptor, builder, modifiable);
        try {
            validateDescriptor();
            setDescription(getFieldDescriptor());
            this.builderList = builderList;
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    protected void validateDescriptor() throws CouldNotPerformException {
        super.validateDescriptor();
        if (getFieldDescriptor().getType() != MESSAGE) {
            throw new CouldNotPerformException("FieldDescriptor[" + getFieldDescriptor().getName() + "] of Message[" +
                    extractSimpleMessageClass(getBuilder()) + "] is not a message type");
        }
    }

    public List<Builder> getBuilderList() {
        return builderList;
    }

    protected void setBuilderList(final List<Message.Builder> newBuilderList) {
        this.builderList = newBuilderList;
    }

    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() throws CouldNotPerformException {
        ObservableList<TreeItem<ValueType>> childList = FXCollections.observableArrayList();

        for (final Message.Builder builder : builderList) {
            childList.add(createChild(builder));
        }

        return childList;
    }

    private BuilderTreeItem createChild(final Message.Builder builder) throws CouldNotPerformException {
        // load tree item by type
        final BuilderTreeItem builderTreeItem = loadTreeItem(getFieldDescriptor(), builder, true);
        // if is modifiable add symbol to remove to child
        updateChildGraphic(builderTreeItem);
        // return tree item
        return builderTreeItem;
    }

    @Override
    protected void addElement() throws CouldNotPerformException {
        try {
            //add the new message builder to the repeated field
            final Message.Builder builder = BuilderProcessor.addDefaultInstanceToRepeatedField(getFieldDescriptor(), getBuilder());
            // if available set value from parent group
            if (getParent() instanceof GroupTreeItem) {
                ((GroupTreeItem<MB>) getParent()).updateElement(builder, this);
            }
            // create tree item and add it
            BuilderTreeItem child = createChild(builder);
            child.setExpanded(true);
            getChildren().add(child);
            // expand new element
            setExpanded(true);
            // trigger update in parent
            setValue(getValueCasted().createNew(getBuilder()));
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not new element for [" + getFieldDescriptor().getName() + "] of Message[" + extractSimpleMessageClass(getBuilder()) + "]!", ex);
        }
    }

    /**
     * Internal update method. This is only called when it is already checked that the newBuilder differs from
     * the old one.
     *
     * @param newBuilder
     * @param newBuilderList
     * @throws CouldNotPerformException
     */
    protected void update(final MB newBuilder, final List<Message.Builder> newBuilderList) throws CouldNotPerformException {
        // this method is only called if the builder is really new thus equal check does not need to be performed

        // update the internal builder
        this.setValue(getValueCasted().createNew(newBuilder));
        setBuilderList(newBuilderList);

        // if children are not yet initialized just change the internal builder and save the new builder list
        if (!childrenInitialized()) {
            return;
        }

        // save current children
        final List<TreeItem<ValueType>> childrenToRemove = new ArrayList<>(getChildren());
        // iterate over new builder list
        for (final Message.Builder builder : newBuilderList) {
            // save if an according child has been found
            boolean childFound = false;
            // iterate over current children
            for (final TreeItem<ValueType> child : getChildren()) {
                // cast to builder tree item because this is a builder list tree item
                final BuilderTreeItem childCasted = (BuilderTreeItem) child;
                // check if the builder of the child matches this one
                if (childCasted.matchesBuilder(builder)) {
                    // save child found, remove from list of removed children, update child
                    childFound = true;
                    childrenToRemove.remove(child);
                    childCasted.update(builder);
                    break;
                }
            }

            // no child matching the builder found so add new child
            if (!childFound) {
                BuilderTreeItem child = createChild(builder);
                if (isExpanded()) {
                    child.getChildren();
                }
                getChildren().add(child);
            }
        }

        // remove all children that do not have a matching builder anymore
        for (final TreeItem<ValueType> child : childrenToRemove) {
            getChildren().remove(child);
        }
    }

    @Override
    public void update(final MB value) throws CouldNotPerformException {
        update(value, BuilderProcessor.extractRepeatedFieldBuilderList(getFieldDescriptor(), value));
    }
}
