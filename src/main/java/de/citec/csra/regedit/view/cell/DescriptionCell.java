/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.cell;

import de.citec.csra.regedit.struct.GenericNodeContainer;
import de.citec.csra.regedit.struct.Node;
import de.citec.csra.regedit.util.FieldDescriptorUtil;

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
            if (item instanceof GenericNodeContainer) {
                GenericNodeContainer container = (GenericNodeContainer) item;
                try {
                    setText(FieldDescriptorUtil.getId(container.getBuilder().build()));
                } catch (Exception ex) {
                    try {
                        setText(FieldDescriptorUtil.getLabel(container.getBuilder()));
                    } catch (Exception exc) {
                    }
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
