package org.dc.bco.registry.editor.visual.provider;

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
import com.google.protobuf.Message;
import org.dc.bco.registry.editor.RegistryEditor;
import org.dc.bco.registry.editor.struct.consistency.StructureConsistencyKeeper;
import org.dc.bco.registry.editor.util.FieldDescriptorUtil;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.LogLevel;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class FieldDescriptorGroup extends AbstractTreeItemDescriptorProvider {

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
    public FieldDescriptorGroup(Message.Builder builder, int... descriptorNumbers) {
        fieldDescriptors = new Descriptors.FieldDescriptor[descriptorNumbers.length];
        Message.Builder test = builder;
        for (int i = 0; i < descriptorNumbers.length - 1; i++) {
            fieldDescriptors[i] = FieldDescriptorUtil.getFieldDescriptor(descriptorNumbers[i], test);
            test = ((GeneratedMessage) test.getField(fieldDescriptors[i])).toBuilder();
        }
        fieldDescriptors[descriptorNumbers.length - 1] = FieldDescriptorUtil.getFieldDescriptor(descriptorNumbers[descriptorNumbers.length - 1], test);
    }

//    /**
//     * Get all values for the group.
//     *
//     * @param builderList the builders from which the values are tested
//     * @return all values for the group
//     */
//    public List<Object> getFieldValues(List<Message.Builder> builderList) {
//        List<Object> values = new ArrayList<>();
//        Object value;
//        for (Message.Builder messageBuilder : builderList) {
//            value = getValue(messageBuilder);
//            if (!values.contains(value)) {
//                values.add(getValue(messageBuilder));
//            }
//        }
//        return values;
//    }

    /**
     * Get the value for the group from one builder.
     *
     * @param builder the builder from which the value is extracted
     * @return the value for the group in that builder
     */
    @Override
    public Object getValue(Message.Builder builder) {
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
    @Override
    public void setValue(Message.Builder builder, Object value) throws InterruptedException {
        Message.Builder mBuilder = builder;
        for (int i = 0; i < fieldDescriptors.length - 1; i++) {
            mBuilder = mBuilder.getFieldBuilder(fieldDescriptors[i]);
        }
        mBuilder.setField(fieldDescriptors[fieldDescriptors.length - 1], value);
        try {
            StructureConsistencyKeeper.keepStructure(builder, fieldDescriptors[fieldDescriptors.length - 1].getName());
        } catch (CouldNotPerformException ex) {
            RegistryEditor.printException(ex, logger, LogLevel.WARN);
        }
    }

    /**
     * Test if the builder has the same value for the group as the given value.
     *
     * @param builder the tested builder
     * @param value the tested value
     * @return if the grouping value for the builder equals value
     */
    @Override
    public boolean hasEqualValue(Message.Builder builder, Object value) {
        return value.equals(getValue(builder));
    }

    @Override
    public String getDescriptor(Message.Builder msg) throws CouldNotPerformException {
        return getValue(msg).toString();
    }

    public Descriptors.FieldDescriptor[] getFieldDescriptors() {
        return fieldDescriptors;
    }
}
