package org.openbase.bco.registry.editor.visual.cell.editing.combobox;

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

import com.google.protobuf.Message;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import rst.domotic.unit.UnitConfigType.UnitConfig;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class UserConfigComboBoxConverter implements MessageComboBoxConverter {

    @Override
    public String getText(Message msg) {
        try {
            return LabelProcessor.getFirstLabel(((UnitConfig) msg).getLabel());
        } catch (NotAvailableException ex) {
            return ((UnitConfig) msg).getId();
        }
    }

    @Override
    public String getValue(Message msg) {
        return (String) msg.getField(ProtoBufFieldProcessor.getFieldDescriptor(UnitConfig.getDefaultInstance(), UnitConfig.ID_FIELD_NUMBER));
    }

}
