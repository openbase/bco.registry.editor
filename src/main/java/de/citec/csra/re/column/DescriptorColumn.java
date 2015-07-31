/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.column;

import de.citec.agm.remote.AgentRegistryRemote;
import de.citec.apm.remote.AppRegistryRemote;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.cellfactory.DescriptionCell;
import de.citec.csra.re.struct.Node;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.scm.remote.SceneRegistryRemote;
import java.util.Comparator;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 *
 * @author thuxohl
 */
public class DescriptorColumn extends Column {

    public DescriptorColumn(DeviceRegistryRemote deviceRegistryRemote, LocationRegistryRemote locationRegistryRemote, SceneRegistryRemote sceneRegistryRemote, AgentRegistryRemote agentRegistryRemote, AppRegistryRemote appRegistryRemote) {
        super("Description");
        this.setPrefWidth(COLUMN_WIDTH);
        this.setCellFactory(new Callback<TreeTableColumn<Node, Node>, TreeTableCell<Node, Node>>() {

            @Override
            public TreeTableCell<Node, Node> call(TreeTableColumn<Node, Node> param) {
                return new DescriptionCell(deviceRegistryRemote, locationRegistryRemote, sceneRegistryRemote, agentRegistryRemote, appRegistryRemote);
            }
        });
        setComparator(new Comparator<Node>() {

            @Override
            public int compare(Node o1, Node o2) {
                return o1.getDescriptor().compareTo(o2.getDescriptor());
            }
        });
    }

}
