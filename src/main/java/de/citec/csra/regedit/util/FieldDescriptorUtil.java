/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.util;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import de.citec.jul.exception.CouldNotPerformException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class FieldDescriptorUtil {

    public static Descriptors.FieldDescriptor getField(final int repeatedFieldNumber, final Message.Builder builder) {
        return builder.getDescriptorForType().findFieldByNumber(repeatedFieldNumber);
    }

    public static Descriptors.FieldDescriptor getField(final int repeatedFieldNumber, final GeneratedMessage message) {
        return getField(repeatedFieldNumber, message.toBuilder());
    }

    public static Descriptors.FieldDescriptor getField(String fieldName, final Message.Builder builder) {
        return builder.getDescriptorForType().findFieldByName(fieldName);
    }

    public static String getId(Message msg) throws CouldNotPerformException {
        try {
            Method method = msg.getClass().getMethod("getId");
            return (String) method.invoke(msg);
        } catch (IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException("Could not get id of [" + msg + "]", ex);
        }
    }

    public static String getDescription(Message.Builder msg) throws CouldNotPerformException {
        try {
            Method method = msg.getClass().getMethod("getDescription");
            return (String) method.invoke(msg);
        } catch (IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException("Could not get description of [" + msg + "]", ex);
        }
    }

    public static String getLabel(Message.Builder msg) throws CouldNotPerformException {
        try {
            Method method = msg.getClass().getMethod("getLabel");
            return (String) method.invoke(msg);
        } catch (IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException("Could not get description of [" + msg + "]", ex);
        }
    }
}
