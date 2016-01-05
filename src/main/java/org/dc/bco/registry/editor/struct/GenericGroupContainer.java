/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.registry.editor.struct;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import org.dc.bco.registry.editor.util.FieldDescriptorUtil;
import org.dc.bco.registry.editor.visual.provider.TreeItemDescriptorProvider;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.InstantiationException;
import java.util.ArrayList;
import java.util.List;

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

    public GenericGroupContainer(String descriptor, int fieldNumber, MB builder, List<RFMB> builderList, TreeItemDescriptorProvider... groups) throws InstantiationException {
        this(descriptor, FieldDescriptorUtil.getFieldDescriptor(fieldNumber, builder), builder, builderList, groups);
    }

    public GenericGroupContainer(String descriptor, Descriptors.FieldDescriptor fieldDescriptor, MB builder, List<RFMB> builderList, TreeItemDescriptorProvider... groups) throws InstantiationException {
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
