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

import org.openbase.bco.registry.editor.struct.GenericNodeContainer;
import org.openbase.bco.registry.editor.struct.Node;
import org.openbase.bco.registry.editor.util.FieldDescriptorUtil;
import org.openbase.jul.exception.CouldNotPerformException;
import rst.authorization.UserConfigType.UserConfig;

/**
 *
 * @author thuxohl
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
                try {
                    String label = FieldDescriptorUtil.getLabel(container.getBuilder());
                    if (!label.isEmpty()) {
                        setText(label);
                    } else {
                        throw new CouldNotPerformException("Id is empty and therefor not good as a descriptor");
                    }
                } catch (Exception ex) {
                    try {
                        String id = FieldDescriptorUtil.getId(container.getBuilder().build());
                        if (!id.isEmpty()) {
                            setText(id);
                        }
                    } catch (Exception exc) {
                    }
                }
                if (((GenericNodeContainer) item).getBuilder() instanceof UserConfig.Builder) {
                    UserConfig.Builder user = (UserConfig.Builder) ((GenericNodeContainer) item).getBuilder();
                    setText(user.getUserName() + " (" + user.getFirstName() + " " + user.getLastName() + ")");
                }
            }

        }
    }

    private String convertDescriptorToReadable(String descriptor) {
        if (descriptor.equals("")) {
            return descriptor;
        }

        descriptor.toLowerCase();
        String result = "";
        String[] split = descriptor.split("_");
        for (int i = 0; i < split.length; i++) {
            result += Character.toUpperCase(split[i].charAt(0));
            result += split[i].substring(1);
        }
        return result;
    }
}
