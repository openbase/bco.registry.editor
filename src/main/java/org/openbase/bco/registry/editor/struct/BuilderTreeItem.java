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
import org.openbase.jul.processing.StringProcessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class BuilderTreeItem<MB extends Message.Builder> extends AbstractBuilderTreeItem<MB> {

    private final Map<FieldDescriptor, GenericTreeItem> descriptorChildMap;

    public BuilderTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        this.descriptorChildMap = new HashMap<>();
    }

    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() throws CouldNotPerformException {
        final ObservableList<TreeItem<ValueType>> childList = FXCollections.observableArrayList();

        //TODO:
        // complete conversion?
        final Set<Integer> filteredFields = getFilteredFields();
        final Set<Integer> uneditableFields = getUneditableFields();
        for (final FieldDescriptor field : getBuilder().getDescriptorForType().getFields()) {
            if (filteredFields.contains(field.getNumber())) {
                continue;
            }

            final GenericTreeItem child = createChild(field, !uneditableFields.contains(field.getNumber()));
            descriptorChildMap.put(field, child);
            childList.add(child);
        }

        return childList;
    }

    protected GenericTreeItem createChild(final FieldDescriptor field, final Boolean editable) throws CouldNotPerformException {
        if (field.isRepeated()) {
            switch (field.getType()) {
                case MESSAGE:
                    return new BuilderListTreeItem<>(field, getBuilder(), editable);
                default:
                    return new ValueListTreeItem<>(field, getBuilder(), editable);
            }
        } else {
            switch (field.getType()) {
                case MESSAGE:
                    // handle if a message only has one repeated field by reducing the tree depth by 1 in these cases
                    final Builder fieldBuilder = getBuilder().getFieldBuilder(field);
                    // check if the builder for the field has only one field and if it is repeated
                    if (fieldBuilder.getDescriptorForType().getFields().size() == 1 && fieldBuilder.getDescriptorForType().getFields().get(0).isRepeated()) {
                        final FieldDescriptor fieldDescriptor = fieldBuilder.getDescriptorForType().getFields().get(0);
                        AbstractListTreeItem treeItem;
                        // differentiate between internal field type
                        switch (fieldDescriptor.getType()) {
                            case MESSAGE:
                                treeItem = new BuilderListTreeItem<>(fieldDescriptor, fieldBuilder, editable);
                                break;
                            default:
                                treeItem = new ValueListTreeItem<>(fieldDescriptor, fieldBuilder, editable);
                        }
                        // update description to the original field name
                        treeItem.setDescription(StringProcessor.transformToCamelCase(field.getName()));
                        return treeItem;
                    }
                    return loadTreeItem(field, fieldBuilder, editable);
                default:
                    return new LeafTreeItem<>(field, getBuilder().getField(field), getBuilder(), editable);
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

    /**
     * Return a set of field numbers for fields that are not editable.
     * This is called once when the children are
     * generated and should be overwritten to set fields uneditable.
     *
     * @return a set of field numbers to be set uneditable
     */
    protected Set<Integer> getUneditableFields() {
        return new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(final MB value) throws CouldNotPerformException {
//        logger.info("Update value [" + value.getClass().getName() + ", " + getClass().getName() + "]");
        this.setValue(getValueCasted().createNew(value));

        // if children are not yet initialized just change the internal builder
        if (!childrenInitialized()) {
            return;
        }

//        logger.info("Children are already initialized");
        for (final FieldDescriptor field : descriptorChildMap.keySet()) {
//            logger.info("Update for field[" + field.getName() + ", " + field.getType().name() + ", " + field.isRepeated() + "]");

            if (field.isRepeated()) {
                descriptorChildMap.get(field).update(value);
            } else {
//                logger.info("Extract class for field: " + value.getField(field).getClass().getName());
                switch (field.getType()) {
                    case MESSAGE:
                        if (!value.hasField(field)) {
                            continue;
                        }
                        descriptorChildMap.get(field).update(value.getFieldBuilder(field));
                        break;
                    default:
                        descriptorChildMap.get(field).update(value.getField(field));
                }
            }
        }
    }

    /**
     * This method can be overwritten to allow a fast check if a builder belongs to the same message.
     * It is used by {@link BuilderListTreeItem} to check which children need to be updated with which builders.
     * The default implementation just checks if the builder equal one another.
     * An example of a good implementation is for example to check if the ids match for unit configs.
     *
     * @param builder the builder which is checked against the current builder of this tree item
     * @return if the builder matches the one of this tree item
     */
    protected boolean matchesBuilder(final MB builder) {
        return false;
    }

    protected Map<FieldDescriptor, GenericTreeItem> getDescriptorChildMap() {
        return descriptorChildMap;
    }
}
