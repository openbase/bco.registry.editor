package org.openbase.bco.registry.editor.struct.converter;

/*-
 * #%L
 * BCO Registry Editor
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
import com.google.protobuf.Descriptors;
import java.util.HashMap;
import java.util.Map;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import rst.domotic.state.EnablingStateType.EnablingState;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class EnablingStateConverter implements Converter {

    private static final String VALUE_FIELD = "value";

    private final EnablingState.Builder enablingState;
    private final Descriptors.FieldDescriptor fieldDescriptor;

    public EnablingStateConverter(EnablingState.Builder enablingState) {
        this.enablingState = enablingState;
        this.fieldDescriptor = ProtoBufFieldProcessor.getFieldDescriptor(enablingState, VALUE_FIELD);
    }

    @Override
    public void updateBuilder(String fieldName, Object value) throws CouldNotPerformException {
        switch (fieldName) {
            case VALUE_FIELD:
                enablingState.setField(fieldDescriptor, value);
                break;
            default:
        }

    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put(VALUE_FIELD, enablingState.getField(fieldDescriptor));
        return fieldMap;
    }

}
