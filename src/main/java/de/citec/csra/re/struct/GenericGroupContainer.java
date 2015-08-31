/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import de.citec.csra.re.util.FieldDescriptorGroup;
import de.citec.csra.re.util.FieldDescriptorUtil;
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

    public GenericGroupContainer(String descriptor, int fieldNumber, MB builder, List<RFMB> builderList, FieldDescriptorGroup... groups) throws InstantiationException {
        this(descriptor, FieldDescriptorUtil.getField(fieldNumber, builder), builder, builderList, groups);
    }

    public GenericGroupContainer(String descriptor, Descriptors.FieldDescriptor fieldDescriptor, MB builder, List<RFMB> builderList, FieldDescriptorGroup... groups) throws InstantiationException {
        super(descriptor, builder);
        this.fieldGroup = groups[0];
        FieldDescriptorGroup childGroups[] = new FieldDescriptorGroup[groups.length - 1];
        for (int i = 1; i < groups.length; i++) {
            childGroups[i - 1] = groups[i];
        }

        try {
            List<RFMB> groupBuilderList = new ArrayList<>();
            List<Object> values = fieldGroup.getFieldValues(builderList);
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

    public GenericGroupContainer(String descriptor, Descriptors.FieldDescriptor fieldDescriptor, MB builder, List<RFMB> builderList, FieldDescriptorGroup group) throws InstantiationException {
        super(descriptor, builder);
        this.fieldGroup = group;
        try {
            List<RFMB> groupBuilderList = new ArrayList<>();
            List<Object> values = fieldGroup.getFieldValues(builderList);
            for (Object value : values) {
                for (RFMB messageBuilder : builderList) {
                    if (fieldGroup.hasEqualValue(messageBuilder, value)) {
                        groupBuilderList.add(messageBuilder);
                    }
                }
                super.add(new GenericListContainer<>(value.toString(), fieldDescriptor, builder, groupBuilderList));
                groupBuilderList.clear();
            }
        } catch (CouldNotPerformException ex) {
            throw new de.citec.jul.exception.InstantiationException(this, ex);
        }
        this.setValue(this);
    }

    public FieldDescriptorGroup getFieldGroup() {
        return fieldGroup;
    }
}
