package org.openbase.bco.registry.editor.util.fieldpath;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2024 openbase.org
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
import org.openbase.type.domotic.unit.device.DeviceClassType.DeviceClass;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DeviceClassCompanyFieldPath extends FieldPathDescriptionProvider {

    public DeviceClassCompanyFieldPath() {
        super(new FieldDescriptorPath(DeviceClass.getDefaultInstance(), DeviceClass.COMPANY_FIELD_NUMBER));
    }

    @Override
    public String generateDescription(final Object value) {
        return (String) value;
    }
}
