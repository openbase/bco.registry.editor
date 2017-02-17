package org.openbase.bco.registry.editor.visual.column;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2017 openbase.org
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
import java.util.Comparator;
import java.util.Map;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import org.openbase.bco.registry.editor.struct.Node;
import org.openbase.bco.registry.editor.struct.NodeContainer;
import org.openbase.bco.registry.editor.visual.cell.DescriptionCell;
import static org.openbase.bco.registry.editor.visual.column.ValueColumn.VALUE_COLUMN_PROPORTION;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DescriptorColumn extends Column {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(DescriptorColumn.class);

    public static final double DESCRIPTOR_COLUMN_PROPORTION = 1 - VALUE_COLUMN_PROPORTION;
    private final Map<String, Integer> fieldPriorityMap;

    public DescriptorColumn() {
        super("");
        this.fieldPriorityMap = new FieldPriorityMap();

        this.setSortable(true);
        this.setSortType(SortType.ASCENDING);
        this.setCellFactory(new Callback<TreeTableColumn<Node, Node>, TreeTableCell<Node, Node>>() {

            @Override
            public TreeTableCell<Node, Node> call(TreeTableColumn<Node, Node> param) {
                try {
                    return new DescriptionCell();
                } catch (InterruptedException ex) {
                    ExceptionPrinter.printHistory(new CouldNotPerformException("Could not build description cell!", ex), logger, LogLevel.WARN);
                    return new TreeTableCell();
                }
            }
        });
        setComparator(new Comparator<Node>() {

            @Override
            public int compare(Node o1, Node o2) {
                if (fieldPriorityMap.get(o1.getDescriptor()) != null && fieldPriorityMap.get(o2.getDescriptor()) == null) {
                    return -1;
                } else if (fieldPriorityMap.get(o1.getDescriptor()) == null && fieldPriorityMap.get(o2.getDescriptor()) != null) {
                    return 1;
                } else if (fieldPriorityMap.get(o1.getDescriptor()) != null && fieldPriorityMap.get(o2.getDescriptor()) != null) {
                    return fieldPriorityMap.get(o1.getDescriptor()) - fieldPriorityMap.get(o2.getDescriptor());
                } else {
                    return o1.getDescriptor().compareTo(o2.getDescriptor());
                }
            }
        });
    }

    @Override
    public void addWidthProperty(ReadOnlyDoubleProperty widthProperty) {
        widthProperty.addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setPrefWidth(newValue.doubleValue() * DESCRIPTOR_COLUMN_PROPORTION);
            }
        });
    }

    private String getLabelOrId(NodeContainer container) {
        String text = "";
        try {
            text = ProtoBufFieldProcessor.getLabel(container.getBuilder());
            if (!text.isEmpty()) {
                return text;
            } else {
                throw new CouldNotPerformException("Id is empty and therefore not good as a descriptor");
            }
        } catch (Exception ex) {
            try {
                text = ProtoBufFieldProcessor.getId(container.getBuilder().build());
                if (!text.isEmpty()) {
                    return text;
                }
            } catch (Exception exc) {
            }
        }
        return text;
    }
}
