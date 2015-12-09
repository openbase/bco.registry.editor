/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.column;

import de.citec.csra.regedit.view.cell.DescriptionCell;
import static de.citec.csra.regedit.view.column.ValueColumn.VALUE_COLUMN_PROPORTION;
import de.citec.csra.regedit.struct.Node;
import de.citec.csra.regedit.struct.NodeContainer;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
import de.citec.jul.exception.CouldNotPerformException;
import java.util.Comparator;
import java.util.Map;
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

    public static final double DESCRIPTOR_COLUMN_PROPORTION = 1 - VALUE_COLUMN_PROPORTION;
    private final Map<String, Integer> fieldPriorityMap = new FieldPriorityMap();

    public DescriptorColumn() {
        super("");

        this.setSortable(true);
        this.setSortType(SortType.ASCENDING);
        this.setCellFactory(new Callback<TreeTableColumn<Node, Node>, TreeTableCell<Node, Node>>() {

            @Override
            public TreeTableCell<Node, Node> call(TreeTableColumn<Node, Node> param) {
                return new DescriptionCell();
            }
        });
        setComparator(new Comparator<Node>() {

            @Override
            public int compare(Node o1, Node o2) {
                if (o1 instanceof NodeContainer && o2 instanceof NodeContainer) {
                    String o1LabelOrId = getLabelOrId((NodeContainer) o1);
                    String o2LabelOrId = getLabelOrId((NodeContainer) o2);
                    if (!(o1LabelOrId.isEmpty() || o2LabelOrId.isEmpty())) {
                        return o1LabelOrId.compareTo(o2LabelOrId);
                    }
                }
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
            text = FieldDescriptorUtil.getLabel(container.getBuilder());
            if (!text.isEmpty()) {
                return text;
            } else {
                throw new CouldNotPerformException("Id is empty and therefore not good as a descriptor");
            }
        } catch (Exception ex) {
            try {
                text = FieldDescriptorUtil.getId(container.getBuilder().build());
                if (!text.isEmpty()) {
                    return text;
                }
            } catch (Exception exc) {
            }
        }
        return text;
    }
}
