/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.util;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 * @param <MB> the builder type that is grouped
 */
public class FieldDescriptorGroup<MB extends GeneratedMessage.Builder<MB>> {

    /**
     * An array of field descriptors whose values will be used for the group.
     */
    Descriptors.FieldDescriptor[] fieldDescriptors;

    /**
     * Create a field group used to group builders of type MB by the given field
     * descriptors in order. E.g. for grouping device configurations after
     * location id's the needed field descriptors are placement_config and
     * location_id.
     *
     * @param fieldDesciptors the descriptors after which the builders are
     * grouped
     */
    public FieldDescriptorGroup(Descriptors.FieldDescriptor... fieldDesciptors) {
        this.fieldDescriptors = fieldDesciptors;
    }

    /**
     * Create a field group used to group builders of type MB by the given field
     * numbers in order. The builder is needed to get the according field
     * descriptors to the numbers. E.g. for grouping device configurations after
     * location id's the needed numbers are
     * DeviceConfig.PLACEMENT_CONFIG_FIELD_NUMBER and
     * PlacementConfig.LOCATION_ID_FIELD_NUMBER. location_id.
     *
     * @param builder an example of the builder that will be grouped
     * @param descriptorNumbers the field numbers leading to the field value for
     * the grouping
     */
    public FieldDescriptorGroup(MB builder, int... descriptorNumbers) {
        fieldDescriptors = new Descriptors.FieldDescriptor[descriptorNumbers.length];
        Message.Builder test = builder;
        for (int i = 0; i < descriptorNumbers.length - 1; i++) {
            fieldDescriptors[i] = FieldDescriptorUtil.getField(descriptorNumbers[i], test);
            test = ((GeneratedMessage) test.getField(fieldDescriptors[i])).toBuilder();
        }
        fieldDescriptors[descriptorNumbers.length - 1] = FieldDescriptorUtil.getField(descriptorNumbers[descriptorNumbers.length - 1], test);
    }

    /**
     * Get all values for the group.
     *
     * @param builderList the builders from which the values are tested
     * @return all values for the group
     */
    public List<Object> getFieldValues(List<MB> builderList) {
        List<Object> values = new ArrayList<>();
        Object value;
        for (MB messageBuilder : builderList) {
            value = getValue(messageBuilder);
            if (!values.contains(value)) {
                values.add(getValue(messageBuilder));
            }
        }
        return values;
    }

    /**
     * Get the value for the group from one builder.
     *
     * @param builder the builder from which the value is extracted
     * @return the value for the group in that builder
     */
    public Object getValue(MB builder) {
        Message.Builder mBuilder = builder;
        Object value;
        for (int i = 0; i < fieldDescriptors.length - 1; i++) {
            mBuilder = ((GeneratedMessage) mBuilder.getField(fieldDescriptors[i])).toBuilder();
        }
        value = mBuilder.getField(fieldDescriptors[fieldDescriptors.length - 1]);
        return value;
    }

    /**
     * Set the value for the group from one builder.
     *
     * @param builder the builder from which the value is set
     * @param value the value set for that field
     */
    public void setValue(MB builder, Object value) {
        Message.Builder mBuilder = builder;
        for (int i = 0; i < fieldDescriptors.length - 1; i++) {
            mBuilder = ((GeneratedMessage) mBuilder.getField(fieldDescriptors[i])).toBuilder();
        }
        mBuilder.setField(fieldDescriptors[fieldDescriptors.length - 1], value);
    }

    /**
     * Test if the builder has the same value for the group as the given value.
     *
     * @param builder the tested builder
     * @param value the tested value
     * @return if the grouping value for the builder equals value
     */
    public boolean hasEqualValue(MB builder, Object value) {
        return value.equals(getValue(builder));
    }
}
