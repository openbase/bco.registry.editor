package org.openbase.bco.registry.editor.struct;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.openbase.bco.registry.editor.visual.provider.TreeItemDescriptorProvider;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class GroupTreeItem<MB extends Message.Builder> extends AbstractTreeItem<MB> {

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

    public GroupTreeItem(FieldDescriptor fieldDescriptor, MB value, List<Builder> builderList, TreeItemDescriptorProvider... groups) {
        super(fieldDescriptor, value);
    }

    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() throws CouldNotPerformException {
        return null;
    }



    public GenericGroupContainer(String descriptor, int fieldNumber, MB builder, List<RFMB> builderList, TreeItemDescriptorProvider... groups) throws InstantiationException, InterruptedException {
        this(descriptor, ProtoBufFieldProcessor.getFieldDescriptor(builder, fieldNumber), builder, builderList, groups);
    }

    public GenericGroupContainer(String descriptor, Descriptors.FieldDescriptor fieldDescriptor, MB builder, List<RFMB> builderList, TreeItemDescriptorProvider... groups) throws InstantiationException, InterruptedException {
        super(descriptor, builder);
        this.displayedDescriptor = descriptor;
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

    public void addItemWithNewGroup(RFM msg) throws CouldNotPerformException {
        try {
            Object value = treeItemDescriptorProvider.getValue(msg);
            values.add(value);
            List<RFMB> groupBuilderList = new ArrayList<>();
            groupBuilderList.add((RFMB) msg.toBuilder());
            if (groups.length == 1) {
                GenericListContainer<MB, GeneratedMessage, RFMB> genericListContainer = new GenericListContainer<>(treeItemDescriptorProvider.getDescriptor(groupBuilderList.get(0)), fieldDescriptor, builder, groupBuilderList);
                valueMap.put(genericListContainer, value);
                super.add(genericListContainer);
            } else {
                GenericGroupContainer<MB, GeneratedMessage, RFMB> genericGroupContainer = new GenericGroupContainer<>(treeItemDescriptorProvider.getDescriptor(groupBuilderList.get(0)), fieldDescriptor, builder, groupBuilderList, childGroups);
                valueMap.put(genericGroupContainer, value);
                super.add(genericGroupContainer);
            }
        } catch (CouldNotPerformException | InterruptedException ex) {
            throw new CouldNotPerformException("Could not add message with new value for this group", ex);
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
