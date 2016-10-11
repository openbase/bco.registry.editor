package org.openbase.bco.registry.editor.visual.cell.editing;

/*
 * #%L
 * RegistryEditor
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
import com.google.protobuf.Message;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import rst.domotic.unit.user.UserConfigType.UserConfig;
import rst.domotic.unit.UnitConfigType.UnitConfig;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class UserConfigComboBoxConverter implements MessageComboBoxConverter {

    @Override
    public String getText(Message msg) {
        String userName = (String) msg.getField(ProtoBufFieldProcessor.getFieldDescriptor(UserConfig.getDefaultInstance(), UserConfig.USER_NAME_FIELD_NUMBER));
        String firstName = (String) msg.getField(ProtoBufFieldProcessor.getFieldDescriptor(UserConfig.getDefaultInstance(), UserConfig.FIRST_NAME_FIELD_NUMBER));
        String lastName = (String) msg.getField(ProtoBufFieldProcessor.getFieldDescriptor(UserConfig.getDefaultInstance(), UserConfig.LAST_NAME_FIELD_NUMBER));
        return userName + " (" + firstName + " " + lastName + ")";
    }

    @Override
    public String getValue(Message msg) {
        return (String) msg.getField(ProtoBufFieldProcessor.getFieldDescriptor(UnitConfig.getDefaultInstance(), UnitConfig.ID_FIELD_NUMBER));
    }

}
