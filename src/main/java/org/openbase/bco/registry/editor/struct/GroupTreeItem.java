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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import org.openbase.bco.registry.editor.util.FieldPathDescriptionProvider;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.protobuf.BuilderProcessor;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class GroupTreeItem<MB extends Message.Builder> extends BuilderListTreeItem<MB> {

    private final Object value;
    private final FieldPathDescriptionProvider parentGroupValueProvider;
    private final FieldPathDescriptionProvider groupValueProvider;
    private final Map<Object, BuilderListTreeItem<MB>> valueChildMap;
    private final FieldPathDescriptionProvider[] childGroups;

    public GroupTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable, final FieldPathDescriptionProvider... groupValueProviders) throws InitializationException {
        this(fieldDescriptor, builder, modifiable, null, null, groupValueProviders);
    }

    private GroupTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable, final Object value, final FieldPathDescriptionProvider parentGroupValueProvider, final FieldPathDescriptionProvider... groupValueProviders) throws InitializationException {
        super(fieldDescriptor, builder, modifiable);

        if (groupValueProviders.length < 1) {
            throw new InitializationException(this, new NotAvailableException("field path descriptors"));
        }

        this.value = value;
        this.parentGroupValueProvider = parentGroupValueProvider;
        this.groupValueProvider = groupValueProviders[0];
        this.childGroups = new FieldPathDescriptionProvider[groupValueProviders.length - 1];
        if (groupValueProviders.length > 1) {
            System.arraycopy(groupValueProviders, 1, childGroups, 0, childGroups.length);
        }
        this.valueChildMap = new HashMap<>();
    }

    private GroupTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable, final List<Message.Builder> builderList, final Object value, final FieldPathDescriptionProvider parentGroupValueProvider, FieldPathDescriptionProvider... groupValueProviders) throws InitializationException {
        this(fieldDescriptor, builder, modifiable, value, parentGroupValueProvider, groupValueProviders);
        super.setBuilderList(builderList);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() throws CouldNotPerformException {
        final ObservableList<TreeItem<ValueType>> childList = FXCollections.observableArrayList();

        for (final Entry<Object, List<Builder>> entry : groupValueProvider.getValueBuilderMap(getBuilderList()).entrySet()) {
            childList.add(createChild(entry.getKey(), entry.getValue()));
        }
        return childList;
    }

    private BuilderListTreeItem<MB> createChild(final Object value, final List<Message.Builder> builderList) throws CouldNotPerformException {
        BuilderListTreeItem<MB> childTreeItem;
        if (childGroups.length == 0) {
            childTreeItem = new BuilderListTreeItem<>(getFieldDescriptor(), getBuilder(), isModifiable(), builderList);
            childTreeItem.setDescriptionGraphic(new Label(groupValueProvider.generateDescription(value)));
        } else {
            childTreeItem = new GroupTreeItem<>(getFieldDescriptor(), getBuilder(), isModifiable(), builderList, value, groupValueProvider, childGroups);
        }
        valueChildMap.put(value, childTreeItem);
        return childTreeItem;
    }

    public void updateElement(final Message.Builder builder) {
        // ignore topmost group
        if (getParent() == null) {
            return;
        }

        // update with value
        parentGroupValueProvider.setValue(builder, value);

        // update with value from parent
        if (getParent() instanceof GroupTreeItem) {
            ((GroupTreeItem) getParent()).updateElement(builder);
        }
    }

    @Override
    protected Node createDescriptionGraphic() {
        return new Label(parentGroupValueProvider.generateDescription(value));
    }

    @Override
    protected void update(final MB newBuilder, final List<Builder> newBuilderList) throws CouldNotPerformException {
        // update the internal builder
        this.setValue(getValueCasted().createNew(newBuilder));
        setBuilderList(newBuilderList);

        // if children are not yet initialized just change the internal builder and save the new builder list
        if (!childrenInitialized()) {
            return;
        }

        // save current values to check if some are not existent anymore
        final Set<Object> removedValueSet = new HashSet<>(valueChildMap.keySet());
        // create value map for new builder list and iterate over it
        for (final Entry<Object, List<Builder>> entry : groupValueProvider.getValueBuilderMap(newBuilderList).entrySet()) {
            if (!valueChildMap.containsKey(entry.getKey())) {
                // new value so add new child
                getChildren().add(createChild(entry.getKey(), entry.getValue()));
            } else {
                // value existent so trigger update in according child
                valueChildMap.get(entry.getKey()).update(newBuilder, entry.getValue());
                // remove from set of removed valued
                removedValueSet.remove(entry.getKey());
            }
        }

        // iterate over removed values
        for (final Object groupValue : removedValueSet) {
            // remove from child map
            final BuilderListTreeItem removedTreeItem = valueChildMap.remove(groupValue);
            // remove tree item from children
            getChildren().remove(removedTreeItem);
        }
    }
}
