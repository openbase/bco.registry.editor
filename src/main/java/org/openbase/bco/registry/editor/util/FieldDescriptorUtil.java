package org.openbase.bco.registry.editor.util;

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
import com.google.protobuf.Message;
import java.util.List;
import org.openbase.jul.exception.CouldNotPerformException;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class FieldDescriptorUtil {

    public static Descriptors.FieldDescriptor getFieldDescriptor(final int repeatedFieldNumber, final Message.Builder builder) {
        return builder.getDescriptorForType().findFieldByNumber(repeatedFieldNumber);
    }

    public static Descriptors.FieldDescriptor getFieldDescriptor(final int repeatedFieldNumber, final GeneratedMessage message) {
        return FieldDescriptorUtil.getFieldDescriptor(repeatedFieldNumber, message.toBuilder());
    }

    public static Descriptors.FieldDescriptor getFieldDescriptor(String fieldName, final Message.Builder builder) {
        return builder.getDescriptorForType().findFieldByName(fieldName);
    }

    public static String getId(Message msg) throws CouldNotPerformException {
        return getId(msg.toBuilder());
    }

    public static String getId(Message.Builder msg) throws CouldNotPerformException {
        try {
            return (String) msg.getField(getFieldDescriptor("id", msg));
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not get id of [" + msg + "]", ex);
        }
    }

    public static String getDescription(Message.Builder msg) throws CouldNotPerformException {
        try {
            return (String) msg.getField(getFieldDescriptor("description", msg));
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not get description of [" + msg + "]", ex);
        }
    }

    public static String getLabel(Message.Builder msg) throws CouldNotPerformException {
        try {
            return (String) msg.getField(getFieldDescriptor("label", msg));
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not get label of [" + msg + "]", ex);
        }
    }

    public static void initRequiredFieldsWithDefault(Message.Builder builder) {
        List<String> missingFieldList = builder.findInitializationErrors();
        missingFieldList.stream().forEach((initError) -> {
            initFieldWithDefault(builder, initError);
        });
    }

    public static Message.Builder initFieldWithDefault(Message.Builder builder, String fieldPath) {
        Descriptors.FieldDescriptor fieldDescriptor;
        Message.Builder tmpBuilder = builder;

        System.out.println("path: " + fieldPath);
        String[] fields = fieldPath.split("\\.");
        System.out.println("split lenght: " + fields.length);

        for (int i = 0; i < fields.length - 1; ++i) {
            if (fields[i].endsWith("]")) {
                String fieldName = fields[i].split("\\[")[0];
                int number = Integer.parseInt(fields[i].split("\\[")[1].split("\\]")[0]);
                fieldDescriptor = FieldDescriptorUtil.getFieldDescriptor(fieldName, tmpBuilder);
                System.out.println("field: " + fieldDescriptor.getName());
                System.out.println("number:" + number);

                Message.Builder subBuilder = ((Message) tmpBuilder.getRepeatedField(fieldDescriptor, number)).toBuilder();
                System.out.println("tmpBuilder: " + tmpBuilder);
                System.out.println("subBuilder: " + subBuilder);
                String subPath = fields[i + 1];
                for (int j = i + 2; j < fields.length; ++j) {
                    subPath += "." + fields[j];
                }
                System.out.println("subPath: " + subPath);
                tmpBuilder.setRepeatedField(fieldDescriptor, number, initFieldWithDefault(subBuilder, subPath).buildPartial());
                return builder;
            } else {
                fieldDescriptor = FieldDescriptorUtil.getFieldDescriptor(fields[i], tmpBuilder);
                System.out.println("Current field: " + fieldDescriptor.getName());
                tmpBuilder = tmpBuilder.getFieldBuilder(fieldDescriptor);
            }
        }
        fieldDescriptor = FieldDescriptorUtil.getFieldDescriptor(fields[fields.length - 1], tmpBuilder);
        Object field = tmpBuilder.getField(fieldDescriptor);
        System.out.println("Set field: " + fieldDescriptor.getName() + " to " + field.toString());
        tmpBuilder.setField(fieldDescriptor, field);
        System.out.println("builder: " + builder);
        System.out.println("tmpbuilder: " + tmpBuilder);
        return builder;
    }

    public static Message.Builder clearRequiredFields(Message.Builder builder) {
        Descriptors.FieldDescriptor fieldDescriptor;
        Message.Builder tmpBuilder;

        for (String fieldPath : builder.findInitializationErrors()) {
            tmpBuilder = builder;
            System.out.println("path: " + fieldPath);
            String[] fields = fieldPath.split("\\.");
            System.out.println("split lenght: " + fields.length);
            boolean alreadyRemoved = false;
            for (int i = 0; i < fields.length - 2; ++i) {
                if (fields[i].endsWith("]")) {
                    String fieldName = fields[i].split("\\[")[0];
                    int number = Integer.parseInt(fields[i].split("\\[")[1].split("\\]")[0]);
                    fieldDescriptor = FieldDescriptorUtil.getFieldDescriptor(fieldName, tmpBuilder);
                    System.out.println("field: " + fieldDescriptor.getName());
                    System.out.println("number:" + number);

                    Message.Builder subBuilder = ((Message) tmpBuilder.getRepeatedField(fieldDescriptor, number)).toBuilder();
                    System.out.println("tmpBuilder: " + tmpBuilder);
                    System.out.println("subBuilder: " + subBuilder);
                    String subPath = fields[i + 1];
                    for (int j = i + 2; j < fields.length; ++j) {
                        subPath += "." + fields[j];
                    }
                    System.out.println("subPath: " + subPath);
                    tmpBuilder.setRepeatedField(fieldDescriptor, number, clearRequiredFields(subBuilder).buildPartial());
                    return builder;
                } else {
                    System.out.println("test: " + fields[i]);
                    fieldDescriptor = FieldDescriptorUtil.getFieldDescriptor(fields[i], tmpBuilder);
                    if (!tmpBuilder.hasField(fieldDescriptor)) {
                        alreadyRemoved = true;
                        continue;
                    }
                    System.out.println("Current field: " + fieldDescriptor.getName());
                    tmpBuilder = tmpBuilder.getFieldBuilder(fieldDescriptor);
                }
            }
            if (alreadyRemoved) {
                System.out.println("Already removed!");
                continue;
            }
            fieldDescriptor = FieldDescriptorUtil.getFieldDescriptor(fields[fields.length - 2], tmpBuilder);
            System.out.println("Clear field: " + fieldDescriptor.getName());
            tmpBuilder.clearField(fieldDescriptor);
            System.out.println("builder: " + builder);
            System.out.println("tmpbuilder: " + tmpBuilder);
            System.out.println("tmp: " + tmpBuilder.buildPartial());
            System.out.println("message: " + builder.buildPartial());
        }

        List<String> missingFieldList = builder.findInitializationErrors();
        String missingFields = "";
        for (String error : missingFieldList) {
            missingFields += "[" + error + "]\n";
        }
        System.out.println("Errors:\n" + missingFields);
        return builder;
    }

    public static boolean onlySomeRequiredFieldsAreSet(Message.Builder builder) {
        if (builder.isInitialized()) {
            // all required fields are set, thus no problem
            return false;
        }

        for (Descriptors.FieldDescriptor field : builder.getDescriptorForType().getFields()) {
            // check if the field is set or a repeated field that does not contain further messages, if not continue
            if (!field.isRepeated() && !builder.hasField(field)) {
                continue;
            }
            if (field.isRepeated() && field.getType() != Descriptors.FieldDescriptor.Type.MESSAGE) {
                continue;
            }

            // recursively check for all sub-messages
            if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
                if (field.isRepeated()) {
                    for (int i = 0; i < builder.getRepeatedFieldCount(field); ++i) {
                        if (onlySomeRequiredFieldsAreSet(((Message) builder.getRepeatedField(field, i)).toBuilder())) {
                            return true;
                        }
                    }
                } else {
                    if (onlySomeRequiredFieldsAreSet(builder.getFieldBuilder(field))) {
                        return true;
                    }
                }
            } else if (field.isRequired()) {
                // field is no message but still required
                return true;
            }
        }

        return false;
    }
}
