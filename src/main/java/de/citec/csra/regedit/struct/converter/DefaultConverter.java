/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.struct.converter;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
import org.dc.jul.exception.CouldNotPerformException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class DefaultConverter implements Converter {

    private final GeneratedMessage.Builder builder;

    public DefaultConverter(GeneratedMessage.Builder builder) {
        this.builder = builder;
    }

    @Override
    public void updateBuilder(String fieldName, Object value) throws CouldNotPerformException {
        try {
            builder.setField(FieldDescriptorUtil.getFieldDescriptor(fieldName, builder), value);
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not update field [" + fieldName + "," + value + "]", ex);
        }
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> fieldMap = new HashMap<>();
        for (Descriptors.FieldDescriptor field : builder.getDescriptorForType().getFields()) {
            fieldMap.put(field.getName(), builder.getField(field));
        }
        return fieldMap;
    }
}
