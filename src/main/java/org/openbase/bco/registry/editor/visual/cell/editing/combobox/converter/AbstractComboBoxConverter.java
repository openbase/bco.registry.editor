package org.openbase.bco.registry.editor.visual.cell.editing.combobox.converter;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2019 openbase.org
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
import org.openbase.bco.registry.editor.RegistryEditorOld;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.FatalImplementationErrorException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractComboBoxConverter<MSG extends Message> implements MessageComboBoxConverter<MSG> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getValue(MSG message) {
        try {
            return ProtoBufFieldProcessor.getId(message);
        } catch (CouldNotPerformException ex) {
            RegistryEditorOld.printException(new FatalImplementationErrorException("Id for message[" + message.getClass().getName() + "] not available", this, ex));
            return "";
        }
    }

}
