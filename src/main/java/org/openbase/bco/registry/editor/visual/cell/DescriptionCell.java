package org.openbase.bco.registry.editor.visual.cell;

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
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import org.openbase.bco.registry.editor.struct.GenericNodeContainer;
import org.openbase.bco.registry.editor.struct.Node;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import rst.domotic.unit.UnitTemplateType.UnitTemplate;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DescriptionCell extends RowCell {

    public DescriptionCell() throws InterruptedException {
        super();
    }

    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            graphicProperty().setValue(null);
            textProperty().setValue("");
            setContextMenu(null);
        } else if (item instanceof Node) {
            setText(convertDescriptorToReadable(item.getDescriptor()));
            //TODO: thuxohl change this part
            if (item instanceof GenericNodeContainer) {
                GenericNodeContainer container = (GenericNodeContainer) item;
                if (container.getBuilder() instanceof UnitTemplate.Builder) {
                    String type = null;
                    try {
                        type = getTypeAsString(container.getBuilder());
                    } catch (CouldNotPerformException ex) {
                    }

                    if (type != null && !type.isEmpty()) {
                        setText(type);
                        return;
                    }
                }

                String label = null;
                try {
                    label = ProtoBufFieldProcessor.getLabel(container.getBuilder());
                } catch (CouldNotPerformException ex) {
                }

                if (label != null && !label.isEmpty()) {
                    setText(label);
                }
            }
        }
    }

    private String getTypeAsString(final Message.Builder msg) throws CouldNotPerformException {
        try {
            return ((Descriptors.EnumValueDescriptor) msg.getField(ProtoBufFieldProcessor.getFieldDescriptor(msg, "type"))).getName();
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not get label of [" + msg + "]", ex);
        }
    }

    private String convertDescriptorToReadable(String descriptor) {
        if (descriptor.equals("")) {
            return descriptor;
        }

        descriptor = descriptor.toLowerCase();
        String result = "";
        String[] split = descriptor.split("_");
        for (String split1 : split) {
            result += Character.toUpperCase(split1.charAt(0));
            result += split1.substring(1);
        }
        return result;
    }
}
