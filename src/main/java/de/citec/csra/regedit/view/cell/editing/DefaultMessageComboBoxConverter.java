/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.cell.editing;

import com.google.protobuf.Message;
import de.citec.csra.regedit.util.FieldDescriptorUtil;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class DefaultMessageComboBoxConverter implements MessageComboBoxConverterInterface {

    @Override
    public String getText(Message msg) {
        return getValue(msg);
    }

    @Override
    public String getValue(Message msg) {
        return (String) msg.getField(FieldDescriptorUtil.getFieldDescriptor("id", msg.toBuilder()));
    }

}
