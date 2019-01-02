package org.openbase.bco.registry.editor.visual.cell.editing.combobox.converter;

/*-
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
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.openbase.type.language.LabelType.Label;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LabelComboBoxConverter<MSG extends Message> extends AbstractComboBoxConverter<MSG> {

    @Override
    public String getText(MSG message) {
        try {
            final Object value = message.getField(ProtoBufFieldProcessor.getFieldDescriptor(message, "label"));
            if (value instanceof Label) {
                return LabelProcessor.getBestMatch((Label) value);
            } else if (value instanceof String) {
                return (String) value;
            } else {
                throw new CouldNotPerformException("Unexpected label type[" + value.getClass().getName() + "]");
            }
        } catch (Exception ex) {
            ExceptionPrinter.printHistory(new CouldNotPerformException("Could not resolve label of [" + message.getClass().getSimpleName() + "]", ex), logger);
            return getValue(message);
        }
    }
}
