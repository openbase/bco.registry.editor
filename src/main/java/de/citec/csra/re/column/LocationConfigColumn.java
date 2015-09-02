/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.column;

import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.cellfactory.LocationConfigCell;
import de.citec.csra.re.struct.node.Node;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 *
 * @author thuxohl
 */
public class LocationConfigColumn extends ValueColumn {

    public LocationConfigColumn(LocationRegistryRemote locationRegistryRemote, ReadOnlyDoubleProperty windowWidthProperty) {
        super(windowWidthProperty);
        setCellFactory(new Callback<TreeTableColumn<Node, Node>, TreeTableCell<Node, Node>>() {

            @Override
            public TreeTableCell<Node, Node> call(TreeTableColumn<Node, Node> param) {
                return new LocationConfigCell(locationRegistryRemote);
            }
        });
    }

}
