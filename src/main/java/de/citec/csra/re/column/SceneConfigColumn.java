/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.column;

import de.citec.csra.re.cellfactory.SceneConfigCell;
import de.citec.csra.re.struct.node.Node;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.scm.remote.SceneRegistryRemote;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 *
 * @author thuxohl
 */
public class SceneConfigColumn extends ValueColumn {

    public SceneConfigColumn(SceneRegistryRemote sceneRegistryRemote, LocationRegistryRemote locationRegistryRemote, ReadOnlyDoubleProperty windowWidthProperty) {
        super(windowWidthProperty);
        this.setCellFactory(new Callback<TreeTableColumn<Node, Node>, TreeTableCell<Node, Node>>() {

            @Override
            public TreeTableCell<Node, Node> call(TreeTableColumn<Node, Node> param) {
                return new SceneConfigCell(sceneRegistryRemote, locationRegistryRemote);
            }
        });
    }

}
