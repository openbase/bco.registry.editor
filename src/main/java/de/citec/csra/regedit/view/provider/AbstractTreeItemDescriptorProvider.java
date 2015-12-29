/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.provider;

import com.google.protobuf.Message;
import org.dc.jul.exception.CouldNotPerformException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public abstract class AbstractTreeItemDescriptorProvider implements TreeItemDescriptorProvider {

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getDescriptor(Message msg) throws CouldNotPerformException {
        return getDescriptor(msg.toBuilder());
    }

    @Override
    public Object getValue(Message msg) throws CouldNotPerformException {
        return getValue(msg.toBuilder());
    }

    @Override
    public boolean hasEqualValue(Message msg, Object value) throws CouldNotPerformException {
        return hasEqualValue(msg.toBuilder(), value);
    }

    @Override
    public List<Object> getValueList(List<Message.Builder> builderList) throws CouldNotPerformException {
        List<Object> values = new ArrayList<>();
        Object value;
        for (Message.Builder messageBuilder : builderList) {
            value = getValue(messageBuilder);
            if (!values.contains(value)) {
                values.add(getValue(messageBuilder));
            }
        }
        return values;
    }
}
