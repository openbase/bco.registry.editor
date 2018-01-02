package org.openbase.bco.registry.editor.visual.provider;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
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
import java.util.List;
import org.openbase.jul.exception.CouldNotPerformException;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public interface TreeItemDescriptorProvider {

    public String getDescriptor(Message msg) throws CouldNotPerformException, InterruptedException;

    public String getDescriptor(Message.Builder msg) throws CouldNotPerformException, InterruptedException;

    public Object getValue(Message msg) throws CouldNotPerformException;

    public Object getValue(Message.Builder msg) throws CouldNotPerformException;

    public void setValue(Message.Builder msg, Object value) throws CouldNotPerformException, InterruptedException;

    public boolean hasEqualValue(Message msg, Object value) throws CouldNotPerformException;

    public boolean hasEqualValue(Message.Builder msg, Object value) throws CouldNotPerformException;

    public List<Object> getValueList(List<Message.Builder> builderList) throws CouldNotPerformException;
}
