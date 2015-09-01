/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re;

import de.citec.csra.re.column.DescriptorColumn;
import de.citec.csra.re.column.ValueColumn;
import javafx.scene.control.TreeTableView;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class RegistryTreeTableView extends TreeTableView {

    public RegistryTreeTableView(TreeTableViewContextMenu.SendableType type) {
        this.setEditable(true);
        this.setShowRoot(false);
        this.getColumns().addAll(new DescriptorColumn(), new ValueColumn());
        if (type != null) {
            this.setContextMenu(new TreeTableViewContextMenu(this, type));
        }
    }
}
