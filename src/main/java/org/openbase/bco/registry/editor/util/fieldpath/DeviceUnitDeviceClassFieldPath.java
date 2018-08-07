package org.openbase.bco.registry.editor.util.fieldpath;

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

import org.openbase.bco.registry.editor.util.FieldDescriptorPath;
import org.openbase.bco.registry.editor.util.FieldPathDescriptionProvider;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.device.DeviceClassType.DeviceClass;
import rst.domotic.unit.device.DeviceConfigType.DeviceConfig;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DeviceUnitDeviceClassFieldPath extends FieldPathDescriptionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceUnitDeviceClassFieldPath.class);

    public DeviceUnitDeviceClassFieldPath() {
        super(new FieldDescriptorPath(UnitConfig.getDefaultInstance(), UnitConfig.DEVICE_CONFIG_FIELD_NUMBER, DeviceConfig.DEVICE_CLASS_ID_FIELD_NUMBER));
    }

    @Override
    public String generateDescription(final Object value) {
        final String deviceClassId = (String) value;
        try {
            final DeviceClass deviceClass = Registries.getClassRegistry().getDeviceClassById(deviceClassId);
            return LabelProcessor.getBestMatch(deviceClass.getLabel());
        } catch (CouldNotPerformException ex) {
            LOGGER.warn("Could not generate description from device class id[" + value + "]", ex);
            return deviceClassId;
        }
    }
}
