package org.dc.bco.registry.editor.visual.column;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 DivineCooperation
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import org.dc.bco.registry.editor.struct.Leaf;
import org.dc.bco.registry.editor.struct.Node;
import org.dc.bco.registry.editor.visual.cell.ValueCell;
import static org.dc.bco.registry.editor.visual.column.DescriptorColumn.logger;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.printer.LogLevel;

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
                try {
                    return new ValueCell();
                } catch (InterruptedException ex) {
                    ExceptionPrinter.printHistory(new CouldNotPerformException("Could not build description cell!", ex), logger, LogLevel.WARN);
                    return new TreeTableCell();
                }
            }
        });
    }

    private class EventHandlerImpl implements EventHandler<TreeTableColumn.CellEditEvent<Node, Node>> {

        @Override
        public void handle(CellEditEvent<Node, Node> event) {
            if (event.getRowValue().getValue() instanceof Leaf) {

                try {
                    ((Leaf) event.getRowValue().getValue()).setValue(((Leaf) event.getNewValue()).getValue());
                } catch (InterruptedException ex) {
                    ExceptionPrinter.printHistory(new CouldNotPerformException("Event handling skipped!", ex), logger, LogLevel.WARN);
                }
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
