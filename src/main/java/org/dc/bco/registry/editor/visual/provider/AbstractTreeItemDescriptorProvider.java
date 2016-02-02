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

import com.google.protobuf.Message;
import java.util.ArrayList;
import java.util.List;
import org.dc.jul.exception.CouldNotPerformException;
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
