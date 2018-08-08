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
import com.google.protobuf.Message.Builder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.protobuf.BuilderProcessor;
import org.openbase.jul.processing.StringProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class BuilderTreeItem<MB extends Message.Builder> extends AbstractBuilderTreeItem<MB> {

    public BuilderTreeItem(final FieldDescriptor fieldDescriptor, final MB builder) throws InitializationException {
        super(fieldDescriptor, builder);
    }

    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() throws CouldNotPerformException {
        final ObservableList<TreeItem<ValueType>> childList = FXCollections.observableArrayList();

        //TODO:
        // complete conversion?
        final Set<Integer> filteredFields = getFilteredFields();
        for (final FieldDescriptor field : getBuilder().getDescriptorForType().getFields()) {
            if (filteredFields.contains(field.getNumber())) {
                continue;
            }

            childList.add(createChild(field));
        }

        return childList;
    }

    protected TreeItem<ValueType> createChild(final FieldDescriptor field) throws CouldNotPerformException {
        if (field.isRepeated()) {
            switch (field.getType()) {
                case MESSAGE:
                    return new BuilderListTreeItem<>(field, getBuilder(), true);
                default:
                    //TODO: leaf type
                    return new GenericTreeItem<>(field, getBuilder().getField(field));
            }
        } else {
            switch (field.getType()) {
                case MESSAGE:
                    //TODO: also implement for values which are not builders and make nicer
                    Builder fieldBuilder = getBuilder().getFieldBuilder(field);
                    if (fieldBuilder.getDescriptorForType().getFields().size() == 1 &&
                            fieldBuilder.getDescriptorForType().getFields().get(0).isRepeated() &&
                            fieldBuilder.getDescriptorForType().getFields().get(0).getType() == Type.MESSAGE) {
                        FieldDescriptor fieldDescriptor = fieldBuilder.getDescriptorForType().getFields().get(0);
                        BuilderListTreeItem mbBuilderListTreeItem = new BuilderListTreeItem(fieldDescriptor, fieldBuilder, true, BuilderProcessor.extractRepeatedFieldBuilderList(fieldDescriptor, fieldBuilder));
                        mbBuilderListTreeItem.setDescription(StringProcessor.transformToCamelCase(field.getName()));
                        return mbBuilderListTreeItem;
                    }
                    return loadTreeItem(field, getBuilder().getFieldBuilder(field));
                default:
                    return new LeafTreeItem<>(field, getBuilder().getField(field), getBuilder());
            }
        }
    }

    /**
     * Return a set of field numbers for fields that are filtered and not added
     * as child tree item. This is called once when the children are
     * generated and should be overwritten to filter fields.
     *
     * @return a set of field numbers to be filtered
     */
    protected Set<Integer> getFilteredFields() {
        return new HashSet<>();
    }
}
