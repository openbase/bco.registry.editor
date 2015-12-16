/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.cell;

import de.citec.csra.regedit.struct.GenericNodeContainer;
import de.citec.csra.regedit.struct.Node;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
import de.citec.jul.exception.CouldNotPerformException;
import rst.authorization.UserConfigType.UserConfig;

/**
 *
 * @author thuxohl
 */
public class DescriptionCell extends RowCell {

    public DescriptionCell() {
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
