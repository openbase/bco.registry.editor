/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.provider;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import de.citec.csra.regedit.RegistryEditor;
import de.citec.csra.regedit.struct.consistency.StructureConsistencyKeeper;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.printer.LogLevel;

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
    public void setValue(Message.Builder builder, Object value) {
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
