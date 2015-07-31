/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.column;

import de.citec.csra.re.cellfactory.DeviceConfigCell;
import de.citec.csra.re.struct.Node;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.lm.remote.LocationRegistryRemote;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 *
 * @author thuxohl
 */
public class DeviceConfigColumn extends ValueColumn {

    public DeviceConfigColumn(DeviceRegistryRemote deviceRegistryRemote, LocationRegistryRemote locationRegistryRemote) {
        super();
        this.setCellFactory(new Callback<TreeTableColumn<Node, Node>, TreeTableCell<Node, Node>>() {

            @Override
            public TreeTableCell<Node, Node> call(TreeTableColumn<Node, Node> param) {
                return new DeviceConfigCell(deviceRegistryRemote, locationRegistryRemote);
            }
        });
    }

}
