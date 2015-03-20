/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re;

import de.citec.csra.dm.remote.DeviceRegistryRemote;
import de.citec.csra.re.cellfactory.DeviceClassCell;
import de.citec.csra.re.struct.node.Node;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 *
 * @author thuxohl
 */
public class DeviceClassColumn extends ValueColumn {

    public DeviceClassColumn(DeviceRegistryRemote remote) {
        super(remote);
        this.setCellFactory(new Callback<TreeTableColumn<Node, Node>, TreeTableCell<Node, Node>>() {

            @Override
            public TreeTableCell<Node, Node> call(TreeTableColumn<Node, Node> param) {
                return new DeviceClassCell(remote);
            }
        });
    }

}
