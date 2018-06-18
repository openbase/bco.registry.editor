package org.openbase.bco.registry.editor.visual.provider;

/*-
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
import org.openbase.bco.registry.editor.util.FieldDescriptorPath;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import rst.domotic.unit.device.DeviceClassType.DeviceClass;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DeviceConfigItemDescriptorProvider extends AbstractTreeItemDescriptorProvider {

    private final FieldDescriptorPath fieldGroup;

    public DeviceConfigItemDescriptorProvider() throws InstantiationException, InterruptedException {
        fieldGroup = new FieldDescriptorPath(UnitConfig.newBuilder(), UnitConfig.UNIT_HOST_ID_FIELD_NUMBER);
    }

    @Override
    public String getDescriptor(Message.Builder msg) throws CouldNotPerformException, InterruptedException {
        UnitConfig deviceUnitConfig = Registries.getUnitRegistry().getUnitConfigById(((UnitConfig.Builder) msg).getUnitHostId(), UnitType.DEVICE);
        DeviceClass deviceClass = Registries.getClassRegistry().getDeviceClassById(deviceUnitConfig.getDeviceConfig().getDeviceClassId());
        return deviceClass.getLabel() + " , " + deviceUnitConfig.getLabel();
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
