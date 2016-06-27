package org.openbase.bco.registry.editor.visual.provider;

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

import com.google.protobuf.Message;
import org.openbase.bco.registry.editor.util.RemotePool;
import org.openbase.jul.exception.CouldNotPerformException;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class DeviceClassItemDescriptorProvider extends AbstractTreeItemDescriptorProvider {

    FieldDescriptorGroup fieldGroup;

    public DeviceClassItemDescriptorProvider() {
        fieldGroup = new FieldDescriptorGroup(DeviceConfig.newBuilder(), DeviceConfig.DEVICE_CLASS_ID_FIELD_NUMBER);
    }

    @Override
    public String getDescriptor(Message.Builder builder) throws CouldNotPerformException, InterruptedException {
        DeviceClass deviceClass = RemotePool.getInstance().getDeviceRemote().getDeviceClassById((String) fieldGroup.getValue(builder));
        return deviceClass.getLabel();
    }

    @Override
    public Object getValue(Message.Builder msg) throws CouldNotPerformException {
        return fieldGroup.getValue(msg);
    }

    @Override
    public void setValue(Message.Builder msg, Object value) throws CouldNotPerformException, InterruptedException {
        fieldGroup.setValue(msg, value);
    }

    @Override
    public boolean hasEqualValue(Message.Builder msg, Object value) throws CouldNotPerformException {
        return value.equals(getValue(msg));
    }
}