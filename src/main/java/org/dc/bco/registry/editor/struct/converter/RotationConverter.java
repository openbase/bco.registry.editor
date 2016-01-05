/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.registry.editor.struct.converter;

import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.processing.QuaternionEulerTransform;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
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
