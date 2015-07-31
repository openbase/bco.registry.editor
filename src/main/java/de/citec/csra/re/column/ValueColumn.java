/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.column;

import de.citec.csra.re.RegistryEditor;
import de.citec.csra.re.struct.Leaf;
import de.citec.csra.re.struct.Node;
import javafx.event.EventHandler;
import javafx.scene.control.TreeTableColumn;

/**
 *
 * @author thuxohl
 */
public abstract class ValueColumn extends Column {

    public ValueColumn() {
        super("Value");
        this.setEditable(true);
        this.setSortable(false);
        this.setOnEditCommit(new EventHandlerImpl());
        this.setPrefWidth(RegistryEditor.RESOLUTION_WIDTH - COLUMN_WIDTH);
    }

    private class EventHandlerImpl implements EventHandler<TreeTableColumn.CellEditEvent<Node, Node>> {

        @Override
        public void handle(CellEditEvent<Node, Node> event) {
            if (event.getRowValue().getValue() instanceof Leaf) {
                ((Leaf) event.getRowValue().getValue()).setValue(((Leaf) event.getNewValue()).getValue());
            }
        }
    }
}
