/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.column;

import de.citec.csra.regedit.view.cell.ValueCell;
import de.citec.csra.regedit.struct.Leaf;
import de.citec.csra.regedit.struct.Node;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 *
 * @author thuxohl
 */
public class ValueColumn extends Column {

    public static final double VALUE_COLUMN_PROPORTION = 0.75;

    public ValueColumn() {
        super("");
        this.setEditable(true);
        this.setSortable(false);
        this.setOnEditCommit(new EventHandlerImpl());
        this.setCellFactory(new Callback<TreeTableColumn<Node, Node>, TreeTableCell<Node, Node>>() {

            @Override
            public TreeTableCell<Node, Node> call(TreeTableColumn<Node, Node> param) {
                return new ValueCell();
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

    @Override
    public void addWidthProperty(ReadOnlyDoubleProperty widthProperty) {
        widthProperty.addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setPrefWidth(newValue.doubleValue() * VALUE_COLUMN_PROPORTION);
            }
        });
    }
}
