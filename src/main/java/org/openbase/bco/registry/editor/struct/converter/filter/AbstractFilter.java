package org.openbase.bco.registry.editor.struct.converter.filter;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2017 openbase.org
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
import com.google.protobuf.Message;
import java.util.HashSet;
import java.util.Set;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractFilter implements Filter {

    private final Set<Descriptors.FieldDescriptor> filteredFieldSet;

    public AbstractFilter() {
        filteredFieldSet = new HashSet<>();
        registerFilteredFields();
    }

    @Override
    public boolean filter(Descriptors.FieldDescriptor fieldDescriptor) {
        return filteredFieldSet.contains(fieldDescriptor);
    }

    protected void registerFilteredField(Descriptors.FieldDescriptor fieldDescriptor) {
        filteredFieldSet.add(fieldDescriptor);
    }

    protected void registerFilteredField(Message msg, int... fieldNumbers) {
        for (int fieldNumber : fieldNumbers) {
            registerFilteredField(ProtoBufFieldProcessor.getFieldDescriptor(msg, fieldNumber));
        }
    }

    protected void removeFilteredField(Descriptors.FieldDescriptor fieldDescriptor) {
        filteredFieldSet.remove(fieldDescriptor);
    }

    protected void removeFilteredField(Message msg, int... fieldNumbers) {
        for (int fieldNumber : fieldNumbers) {
            removeFilteredField(ProtoBufFieldProcessor.getFieldDescriptor(msg, fieldNumber));
        }
    }

    protected abstract void registerFilteredFields();
}
