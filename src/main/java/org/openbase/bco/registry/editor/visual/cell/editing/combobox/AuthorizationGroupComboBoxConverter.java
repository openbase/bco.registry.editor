package org.openbase.bco.registry.editor.visual.cell.editing.combobox;

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

import com.google.protobuf.Message;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import rst.domotic.unit.UnitConfigType.UnitConfig;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.de">Tamino Huxohl</a>
 */
public class AuthorizationGroupComboBoxConverter implements MessageComboBoxConverter {

    @Override
    public String getText(Message msg) {
        try {
            return (String) msg.getField(ProtoBufFieldProcessor.getFieldDescriptor(UnitConfig.getDefaultInstance(), UnitConfig.LABEL_FIELD_NUMBER));
        } catch (Exception ex) {
            return "?";
        }
    }

    @Override
    public String getValue(Message msg) {
        return (String) msg.getField(ProtoBufFieldProcessor.getFieldDescriptor(UnitConfig.getDefaultInstance(), UnitConfig.ID_FIELD_NUMBER));
    }
    
}
