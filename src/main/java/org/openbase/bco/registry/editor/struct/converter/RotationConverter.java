package org.openbase.bco.registry.editor.struct.converter;

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
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.processing.QuaternionEulerTransform;
import rst.geometry.RotationType.Rotation;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RotationConverter implements Converter {

    private final Rotation.Builder rotation;
    private final Vector3d euler;

    private static final String ROLL = "roll";
    private static final String PITCH = "pitch";
    private static final String YAW = "yaw";

    public RotationConverter(Rotation.Builder rotation) {
        this.rotation = rotation;
        /* Even though the rotation type has defined default values they do not seem to be used.
         * Thus assume the default rotation as none.
         * Tts enough to check if only qw is not set because these fields are required.
         * So if one is not set then every other value is not set either.
         */
        if (!rotation.hasQw()) {
            euler = new Vector3d(0.0, 0.0, 0.0);
        } else {
            euler = QuaternionEulerTransform.transform(new Quat4d(rotation.getQx(), rotation.getQy(), rotation.getQz(), rotation.getQw()));
        }
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
