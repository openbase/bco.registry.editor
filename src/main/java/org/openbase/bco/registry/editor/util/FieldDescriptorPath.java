package org.openbase.bco.registry.editor.util;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2024 openbase.org
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
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class FieldDescriptorPath {

    /**
     * An array of field descriptors whose values will be used for the group.
     */
    private final FieldDescriptor[] fieldDescriptors;

    /**
     * Create a field group used to group builders of type MB by the given field
     * descriptors in order. E.g. for grouping device configurations after
     * location id's the needed field descriptors are placement_config and
     * location_id.
     *
     * @param fieldDescriptors the descriptors after which the builders are
     *                         grouped
     */
    public FieldDescriptorPath(final FieldDescriptor... fieldDescriptors) {
        this.fieldDescriptors = fieldDescriptors;
    }

    /**
     * Create a field group used to group builders of type MB by the given field
     * numbers in order. The builder is needed to get the according field
     * descriptors to the numbers. E.g. for grouping device configurations after
     * location id's the needed numbers are
     * DeviceConfig.PLACEMENT_CONFIG_FIELD_NUMBER and
     * PlacementConfig.LOCATION_ID_FIELD_NUMBER. location_id.
     *
     * @param messageOrBuilder  an example of the builder that will be grouped
     * @param descriptorNumbers the field numbers leading to the field value for
     *                          the grouping
     */
    public FieldDescriptorPath(final MessageOrBuilder messageOrBuilder, final Integer... descriptorNumbers) {
        fieldDescriptors = new Descriptors.FieldDescriptor[descriptorNumbers.length];

        MessageOrBuilder internalMessageOrBuilder = messageOrBuilder;
        for (int i = 0; i < descriptorNumbers.length; i++) {
            fieldDescriptors[i] = ProtoBufFieldProcessor.getFieldDescriptor(internalMessageOrBuilder, descriptorNumbers[i]);
            if (i < descriptorNumbers.length - 1) {
                internalMessageOrBuilder = (MessageOrBuilder) messageOrBuilder.getField(fieldDescriptors[i]);
            }
        }
    }

    /**
     * Get the value for the group from one builder.
     *
     * @param messageOrBuilder the builder from which the value is extracted
     * @return the value for the group in that builder
     */
    public Object getValue(final MessageOrBuilder messageOrBuilder) {
        MessageOrBuilder internalMessageOrBuilder = messageOrBuilder;
        for (int i = 0; i < fieldDescriptors.length - 1; i++) {
            try {
                internalMessageOrBuilder = (Message) internalMessageOrBuilder.getField(fieldDescriptors[i]);
            } catch (IllegalArgumentException ex) {
                System.out.println("Could not extract [" + fieldDescriptors[i].getName() + "] from [" + internalMessageOrBuilder.getClass().getName() + "]");
            }
        }
        try {
            return internalMessageOrBuilder.getField(fieldDescriptors[fieldDescriptors.length - 1]);
        } catch (IllegalArgumentException ex) {
            System.out.println("Could not extract [" + fieldDescriptors[fieldDescriptors.length - 1].getName() + "] from [" + internalMessageOrBuilder.getClass().getName() + "]");
            throw ex;
        }
    }

    /**
     * Set the value for the group from one builder.
     *
     * @param builder the builder from which the value is set
     * @param value   the value set for that field
     */
    public void setValue(final Message.Builder builder, final Object value) {
        Message.Builder internalBuilder = builder;
        for (int i = 0; i < fieldDescriptors.length - 1; i++) {
            internalBuilder = internalBuilder.getFieldBuilder(fieldDescriptors[i]);
        }
        internalBuilder.setField(fieldDescriptors[fieldDescriptors.length - 1], value);
    }

    /**
     * Test if the builder has the same value for the group as the given value.
     *
     * @param messageOrBuilder the tested builder
     * @param value            the tested value
     * @return if the grouping value for the builder equals value
     */
    public boolean hasEqualValueDescriptors(final MessageOrBuilder messageOrBuilder, Object value) {
        return value.equals(getValue(messageOrBuilder));
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < fieldDescriptors.length - 1; i++) {
            stringBuilder.append(fieldDescriptors[i].getName()).append(" -> ");
        }

        if(fieldDescriptors.length > 0) {
            stringBuilder.append(fieldDescriptors[fieldDescriptors.length - 1].getName());
        }

        return stringBuilder.toString();
    }
}
