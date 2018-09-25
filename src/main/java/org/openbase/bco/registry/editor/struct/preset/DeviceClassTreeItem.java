package org.openbase.bco.registry.editor.struct.preset;

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

import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openbase.bco.registry.editor.struct.RegistryMessageTreeItem;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.rst.processing.MultiLanguageTextProcessor;
import rst.domotic.unit.device.DeviceClassType.DeviceClass;
import rst.domotic.unit.device.DeviceClassType.DeviceClass.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DeviceClassTreeItem extends RegistryMessageTreeItem<DeviceClass.Builder> {

    public DeviceClassTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        addEventHandler(valueChangedEvent(), event -> updateValueGraphic());
    }

    @Override
    protected Node createValueGraphic() {
        final Node valueGraphic = super.createValueGraphic();
        if (valueGraphic == null) {
            try {
                return new Label(MultiLanguageTextProcessor.getBestMatch(getBuilder().getDescription()));
            } catch (NotAvailableException e) {
                return new Label();
            }
        }
        return valueGraphic;
    }
}
