/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.converter;

import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.processing.QuaternionEulerTransform;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import rst.geometry.RotationType.Rotation;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class QuaternionEulerConverter implements Converter {

    private final Rotation.Builder rotation;
    private final Vector3d euler;

    private static final String ROLL = "roll";
    private static final String PITCH = "pitch";
    private static final String YAW = "yaw";

    public QuaternionEulerConverter(Rotation.Builder rotation) {
        this.rotation = rotation;
        euler = QuaternionEulerTransform.transform(new Quat4d(rotation.getQw(), rotation.getQx(), rotation.getQy(), rotation.getQz()));
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
    public List<ValueTupel> getFields() {
        List<ValueTupel> fields = new ArrayList<>();
        fields.add(new ValueTupel(Math.toDegrees(euler.x), ROLL));
        fields.add(new ValueTupel(Math.toDegrees(euler.y), PITCH));
        fields.add(new ValueTupel(Math.toDegrees(euler.z), YAW));
        return fields;
    }
}
