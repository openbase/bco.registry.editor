/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.registry.editor.visual.provider;

import com.google.protobuf.Message;
import org.dc.jul.exception.CouldNotPerformException;
import java.util.List;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public interface TreeItemDescriptorProvider {

    public String getDescriptor(Message msg) throws CouldNotPerformException;

    public String getDescriptor(Message.Builder msg) throws CouldNotPerformException;

    public Object getValue(Message msg) throws CouldNotPerformException;

    public Object getValue(Message.Builder msg) throws CouldNotPerformException;

    public void setValue(Message.Builder msg, Object value) throws CouldNotPerformException;

    public boolean hasEqualValue(Message msg, Object value) throws CouldNotPerformException;

    public boolean hasEqualValue(Message.Builder msg, Object value) throws CouldNotPerformException;

    public List<Object> getValueList(List<Message.Builder> builderList) throws CouldNotPerformException;
}
