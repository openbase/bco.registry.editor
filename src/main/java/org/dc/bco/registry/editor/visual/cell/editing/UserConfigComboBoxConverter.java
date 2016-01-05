/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.registry.editor.visual.cell.editing;

import com.google.protobuf.Message;
import org.dc.bco.registry.editor.util.FieldDescriptorUtil;
import rst.authorization.UserConfigType.UserConfig;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class UserConfigComboBoxConverter implements MessageComboBoxConverterInterface {

    @Override
    public String getText(Message msg) {
        String userName = (String) msg.getField(FieldDescriptorUtil.getFieldDescriptor(UserConfig.USER_NAME_FIELD_NUMBER, UserConfig.getDefaultInstance()));
        String firstName = (String) msg.getField(FieldDescriptorUtil.getFieldDescriptor(UserConfig.FIRST_NAME_FIELD_NUMBER, UserConfig.getDefaultInstance()));
        String lastName = (String) msg.getField(FieldDescriptorUtil.getFieldDescriptor(UserConfig.LAST_NAME_FIELD_NUMBER, UserConfig.getDefaultInstance()));
        return userName + " (" + firstName + " " + lastName + ")";
    }

    @Override
    public String getValue(Message msg) {
        return (String) msg.getField(FieldDescriptorUtil.getFieldDescriptor(UserConfig.ID_FIELD_NUMBER, UserConfig.getDefaultInstance()));
    }

}
