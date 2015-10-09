/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.struct;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import de.citec.csra.regedit.util.FieldDescriptorGroup;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.InstantiationException;
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
    private final FieldDescriptorGroup fieldGroup;
    /**
     * A list for all the values in the group.
     */
    private final List values;
    private final Descriptors.FieldDescriptor fieldDescriptor;

    public GenericGroupContainer(String descriptor, int fieldNumber, MB builder, List<RFMB> builderList, FieldDescriptorGroup... groups) throws InstantiationException {
        this(descriptor, FieldDescriptorUtil.getFieldDescriptor(fieldNumber, builder), builder, builderList, groups);
    }

    public GenericGroupContainer(String descriptor, Descriptors.FieldDescriptor fieldDescriptor, MB builder, List<RFMB> builderList, FieldDescriptorGroup... groups) throws InstantiationException {
        super(descriptor, builder);
        this.fieldGroup = groups[0];
        this.fieldDescriptor = fieldDescriptor;
        FieldDescriptorGroup childGroups[] = new FieldDescriptorGroup[groups.length - 1];
        for (int i = 1; i < groups.length; i++) {
            childGroups[i - 1] = groups[i];
        }
        try {
            List<RFMB> groupBuilderList = new ArrayList<>();
            values = fieldGroup.getFieldValues(builderList);
            for (Object value : values) {
                for (RFMB messageBuilder : builderList) {
                    if (fieldGroup.hasEqualValue(messageBuilder, value)) {
                        groupBuilderList.add(messageBuilder);
                    }
                }
                if (groups.length == 1) {
                    super.add(new GenericListContainer<>(value.toString(), fieldDescriptor, builder, groupBuilderList));
                } else {
                    super.add(new GenericGroupContainer<>(value.toString(), fieldDescriptor, builder, groupBuilderList, childGroups));
                }
                groupBuilderList.clear();
            }
        } catch (CouldNotPerformException ex) {
            throw new de.citec.jul.exception.InstantiationException(this, ex);
        }
    }

    public FieldDescriptorGroup getFieldGroup() {
        return fieldGroup;
    }

    public List getValues() {
        return values;
    }

    public Descriptors.FieldDescriptor getFieldDescriptor() {
        return fieldDescriptor;
    }
}
