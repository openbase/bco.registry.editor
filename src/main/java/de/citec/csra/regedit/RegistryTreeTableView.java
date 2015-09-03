/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit;

import de.citec.csra.regedit.column.Column;
import de.citec.csra.regedit.column.DescriptorColumn;
import de.citec.csra.regedit.column.ValueColumn;
import de.citec.csra.regedit.struct.Node;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.TreeTableView;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class RegistryTreeTableView extends TreeTableView<Node> {

    public RegistryTreeTableView(TreeTableViewContextMenu.SendableType type) {
        this.setEditable(true);
        this.setShowRoot(false);
        this.getColumns().addAll(new DescriptorColumn(), new ValueColumn());
        if (type != null) {
            this.setContextMenu(new TreeTableViewContextMenu(this, type));
        }
    }

    public void addWidthProperty(ReadOnlyDoubleProperty widthProperty) {
        for (Object column : getColumns()) {
            ((Column) column).addWidthProperty(widthProperty);
        }
    }
}
