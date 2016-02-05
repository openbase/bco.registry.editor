package org.dc.bco.registry.editor.struct;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 DivineCooperation
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
import java.util.List;
import org.dc.bco.registry.editor.util.FieldDescriptorUtil;
import org.dc.bco.registry.editor.visual.provider.TreeItemDescriptorProvider;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.InstantiationException;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
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
    private final Descriptors.FieldDescriptor fieldDescriptor;

    public GenericGroupContainer(String descriptor, int fieldNumber, MB builder, List<RFMB> builderList, TreeItemDescriptorProvider... groups) throws InstantiationException, InterruptedException {
        this(descriptor, FieldDescriptorUtil.getFieldDescriptor(fieldNumber, builder), builder, builderList, groups);
    }

    public GenericGroupContainer(String descriptor, Descriptors.FieldDescriptor fieldDescriptor, MB builder, List<RFMB> builderList, TreeItemDescriptorProvider... groups) throws InstantiationException, InterruptedException {
        super(descriptor, builder);
        this.treeItemDescriptorProvider = groups[0];
        this.fieldDescriptor = fieldDescriptor;
        TreeItemDescriptorProvider childGroups[] = new TreeItemDescriptorProvider[groups.length - 1];
        for (int i = 1; i < groups.length; i++) {
            childGroups[i - 1] = groups[i];
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
                    super.add(new GenericListContainer<>(treeItemDescriptorProvider.getDescriptor(groupBuilderList.get(0)), fieldDescriptor, builder, groupBuilderList));
                } else {
                    super.add(new GenericGroupContainer<>(treeItemDescriptorProvider.getDescriptor(groupBuilderList.get(0)), fieldDescriptor, builder, groupBuilderList, childGroups));
                }
                groupBuilderList.clear();
            }
        } catch (CouldNotPerformException ex) {
            throw new org.dc.jul.exception.InstantiationException(this, ex);
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
}
