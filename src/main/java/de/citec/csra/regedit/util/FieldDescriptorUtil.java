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
        try {
//            Method method = msg.getClass().getMethod("getId");
            return (String) msg.getField(getFieldDescriptor("id", msg.toBuilder()));
//            return (String) method.invoke(msg);
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not get id of [" + msg + "]", ex);
        }
    }

    public static String getDescription(Message.Builder msg) throws CouldNotPerformException {
        try {
//            Method method = msg.getClass().getMethod("getDescription");
            return (String) msg.getField(getFieldDescriptor("description", msg));
//            return (String) method.invoke(msg);
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not get description of [" + msg + "]", ex);
        }
    }

    public static String getLabel(Message.Builder msg) throws CouldNotPerformException {
        try {
//            Method method = msg.getClass().getMethod("getLabel");
            return (String) msg.getField(getFieldDescriptor("label", msg));
//            return (String) method.invoke(msg);
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not get label of [" + msg + "]", ex);
        }
    }
}
