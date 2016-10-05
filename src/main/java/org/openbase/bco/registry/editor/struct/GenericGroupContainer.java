package org.openbase.bco.registry.editor.struct;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 openbase.org
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
import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openbase.bco.registry.editor.visual.provider.TreeItemDescriptorProvider;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 * @param <MB>
 * @param <RFM>
 * @param <RFMB>
 */
public class GenericGroupContainer<MB extends GeneratedMessage.Builder<MB>, RFM extends GeneratedMessage, RFMB extends RFM.Builder<RFMB>> extends NodeContainer<MB> {

    /**
     * A group of field after which values the builder list will be grouped.
     */
    private final TreeItemDescriptorProvider treeItemDescriptorProvider;
    /**
     * A list for all the values in the group.
     */
    private final List values;
    private final Map<NodeContainer, Object> valueMap = new HashMap<>();
    private final TreeItemDescriptorProvider[] groups;
    private final TreeItemDescriptorProvider[] childGroups;
    private final Descriptors.FieldDescriptor fieldDescriptor;

    public GenericGroupContainer(String descriptor, int fieldNumber, MB builder, List<RFMB> builderList, TreeItemDescriptorProvider... groups) throws InstantiationException, InterruptedException {
        this(descriptor, ProtoBufFieldProcessor.getFieldDescriptor(builder, fieldNumber), builder, builderList, groups);
    }

    public GenericGroupContainer(String descriptor, Descriptors.FieldDescriptor fieldDescriptor, MB builder, List<RFMB> builderList, TreeItemDescriptorProvider... groups) throws InstantiationException, InterruptedException {
        super(descriptor, builder);
        this.groups = groups;
        this.treeItemDescriptorProvider = groups[0];
        this.fieldDescriptor = fieldDescriptor;
        this.childGroups = new TreeItemDescriptorProvider[groups.length - 1];
        for (int i = 1; i < groups.length; i++) {
            this.childGroups[i - 1] = groups[i];
        }
        try {
            List<RFMB> groupBuilderList = new ArrayList<>();
            values = treeItemDescriptorProvider.getValueList(new ArrayList<>(builderList));
            for (Object value : values) {
                for (RFMB messageBuilder : builderList) {
                    if (treeItemDescriptorProvider.hasEqualValue(messageBuilder, value)) {
                        groupBuilderList.add(messageBuilder);
                    }
                }
                if (groups.length == 1 && !groupBuilderList.isEmpty()) {
                    GenericListContainer<MB, GeneratedMessage, RFMB> genericListContainer = new GenericListContainer<>(treeItemDescriptorProvider.getDescriptor(groupBuilderList.get(0)), fieldDescriptor, builder, groupBuilderList);
                    valueMap.put(genericListContainer, value);
                    super.add(genericListContainer);
                } else {
                    GenericGroupContainer<MB, GeneratedMessage, RFMB> genericGroupContainer = new GenericGroupContainer<>(treeItemDescriptorProvider.getDescriptor(groupBuilderList.get(0)), fieldDescriptor, builder, groupBuilderList, childGroups);
                    valueMap.put(genericGroupContainer, value);
                    super.add(genericGroupContainer);
                }
                groupBuilderList.clear();
            }
        } catch (CouldNotPerformException ex) {
            throw new org.openbase.jul.exception.InstantiationException(this, ex);
        }
    }

    public TreeItemDescriptorProvider getFieldGroup() {
        return treeItemDescriptorProvider;
    }

    public List getValues() {
        return values;
    }

    public Descriptors.FieldDescriptor getFieldDescriptor() {
        return fieldDescriptor;
    }

    public boolean isLastGroup() {
        return groups.length == 1;
    }

    public TreeItemDescriptorProvider[] getGroups() {
        return groups;
    }

    public TreeItemDescriptorProvider[] getChildGroups() {
        return childGroups;
    }

    public Map<NodeContainer, Object> getValueMap() {
        return valueMap;
    }
}
