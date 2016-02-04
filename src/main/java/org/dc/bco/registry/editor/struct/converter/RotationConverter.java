package org.dc.bco.registry.editor.struct.converter;

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

import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.processing.QuaternionEulerTransform;
import org.slf4j.LoggerFactory;
import rst.geometry.RotationType.Rotation;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class RotationConverter implements Converter {

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    private final Rotation.Builder rotation;
    private final Vector3d euler;

    private static final String ROLL = "roll";
    private static final String PITCH = "pitch";
    private static final String YAW = "yaw";

    public RotationConverter(Rotation.Builder rotation) {
        this.rotation = rotation;
        euler = QuaternionEulerTransform.transform(new Quat4d(rotation.getQx(), rotation.getQy(), rotation.getQz(), rotation.getQw()));
    }

    @Override
    public void updateBuilder(String fieldName, Object value) throws CouldNotPerformException {
        try {
            switch (fieldName) {
                case ROLL:
                    euler.x = Math.toRadians((double) value);
                    break;
                case PITCH:
                    euler.y = Math.toRadians((double) value);
                    break;
                case YAW:
                    euler.z = Math.toRadians((double) value);
                    break;
                default:
            }
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not update rotation with [" + fieldName + "," + value + "]", ex);
        }

        Quat4d quaternion = QuaternionEulerTransform.transform(euler);
        rotation.setQw(quaternion.w);
        rotation.setQx(quaternion.x);
        rotation.setQy(quaternion.y);
        rotation.setQz(quaternion.z);
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put(ROLL, Math.toDegrees(euler.x));
        fieldMap.put(PITCH, Math.toDegrees(euler.y));
        fieldMap.put(YAW, Math.toDegrees(euler.z));
        return fieldMap;
    }
}
