/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.column;

import de.citec.csra.re.struct.leaf.Leaf;
import de.citec.csra.re.struct.node.Node;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TreeTableColumn;

/**
 *
 * @author thuxohl
 */
public abstract class ValueColumn extends Column {

    public static final double VALUE_COLUMN_PROPORTEN = 0.75;

    public ValueColumn(ReadOnlyDoubleProperty windowWidthProperty) {
        super("Value", windowWidthProperty);
        this.setEditable(true);
        this.setSortable(false);
        this.setOnEditCommit(new EventHandlerImpl());
        windowWidthProperty.addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                setPrefWidth(t1.doubleValue() * VALUE_COLUMN_PROPORTEN);
            }
        });
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
