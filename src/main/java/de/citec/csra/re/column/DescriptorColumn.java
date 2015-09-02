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
import static de.citec.csra.re.column.ValueColumn.VALUE_COLUMN_PROPORTEN;
import de.citec.csra.re.struct.node.Node;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.scm.remote.SceneRegistryRemote;
import java.util.Comparator;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 *
 * @author thuxohl
 */
public class DescriptorColumn extends Column {

    public DescriptorColumn(DeviceRegistryRemote deviceRegistryRemote, LocationRegistryRemote locationRegistryRemote, SceneRegistryRemote sceneRegistryRemote, AgentRegistryRemote agentRegistryRemote, AppRegistryRemote appRegistryRemote, ReadOnlyDoubleProperty windowWidthProperty, String type) {
        super("Description", windowWidthProperty);
        windowWidthProperty.addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                setPrefWidth(t1.doubleValue() * (1 - VALUE_COLUMN_PROPORTEN));
            }
        });
        this.setCellFactory(new Callback<TreeTableColumn<Node, Node>, TreeTableCell<Node, Node>>() {

            @Override
            public TreeTableCell<Node, Node> call(TreeTableColumn<Node, Node> param) {
                return new DescriptionCell(deviceRegistryRemote, locationRegistryRemote, sceneRegistryRemote, agentRegistryRemote, appRegistryRemote, type);
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
