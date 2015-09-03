/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.cellfactory;

import de.citec.csra.regedit.util.SelectableLabel;
import de.citec.csra.regedit.struct.Node;
import javafx.scene.control.Label;

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
