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
import org.openbase.bco.registry.editor.struct.value.DescriptionGenerator;
import org.openbase.bco.registry.editor.util.FieldPathDescriptionProvider;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class GroupTreeItem<MB extends Message.Builder> extends BuilderListTreeItem<MB> {

    private final Object value;
    private final FieldPathDescriptionProvider groupValueProvider;
    private final FieldPathDescriptionProvider[] childGroupValueProviders;
    private final Map<Object, BuilderListTreeItem> valueChildMap;

    public GroupTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable, final FieldPathDescriptionProvider... groupValueProviders) throws InitializationException {
        super(fieldDescriptor, builder, modifiable);

        this.value = null;
        this.groupValueProvider = null;
        this.childGroupValueProviders = groupValueProviders;
        this.valueChildMap = new HashMap<>();
    }

    private GroupTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable, final List<Message.Builder> builderList, final Object value, final FieldPathDescriptionProvider groupValueProvider, FieldPathDescriptionProvider... groupValueProviders) throws InitializationException {
        super(fieldDescriptor, builder, modifiable, builderList);

        this.value = value;
        this.groupValueProvider = groupValueProvider;
        this.childGroupValueProviders = groupValueProviders;
        this.valueChildMap = new HashMap<>();
    }


    @SuppressWarnings("unchecked")
    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() throws CouldNotPerformException {
        final ObservableList<TreeItem<ValueType>> childList = FXCollections.observableArrayList();
        final FieldPathDescriptionProvider[] childGroups = new FieldPathDescriptionProvider[childGroupValueProviders.length - 1];
        if (childGroupValueProviders.length > 1) {
            System.arraycopy(childGroupValueProviders, 1, childGroups, 0, childGroups.length);
        }

        for (final Entry<Object, List<Builder>> entry : childGroupValueProviders[0].getValueBuilderMap(getBuilderList()).entrySet()) {
            BuilderListTreeItem childTreeItem;
            if (childGroups.length == 0) {
                childTreeItem = new BuilderListTreeItem(getFieldDescriptor(), getBuilder(), isModifiable(), entry.getValue()) {

                    @Override
                    public Node getDescriptionGraphic() {
                        return new Label(entry.getKey() == null ? "" : childGroupValueProviders[0].generateDescription(entry.getKey()));
                    }
                };
            } else {
                childTreeItem = new GroupTreeItem(getFieldDescriptor(), getBuilder(), isModifiable(), entry.getValue(), entry.getKey(), childGroupValueProviders[0], childGroups) {

                    @Override
                    public Node getDescriptionGraphic() {
                        return new Label(entry.getKey() == null ? "" : childGroupValueProviders[0].generateDescription(entry.getKey()));
                    }
                };
            }
            childList.add(childTreeItem);
            valueChildMap.put(entry.getKey(), childTreeItem);
        }
        return childList;
    }

    public void updateElement(final Message.Builder builder) {
        // ignore topmost group
        if (groupValueProvider == null) {
            return;
        }

        // update with value
        groupValueProvider.setValue(builder, value);

        // update with value from parent
        if (getParent() instanceof GroupTreeItem) {
            ((GroupTreeItem) getParent()).updateElement(builder);
        }
    }

    public class GroupDescriptionGenerator implements DescriptionGenerator<MB> {

        final String description;

        public GroupDescriptionGenerator(final FieldPathDescriptionProvider groupValueProvider, final Object value) {
            this.description = value == null ? "" : groupValueProvider.generateDescription(value);
        }

        @Override
        public String getValueDescription(MB value) {
            return "";
        }

        @Override
        public String getDescription(MB value) {
            return description;
        }
    }
}
