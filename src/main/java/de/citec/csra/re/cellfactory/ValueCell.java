/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.csra.re.cellfactory.editing.DecimalTextField;
import de.citec.csra.re.cellfactory.editing.EnumComboBox;
import de.citec.csra.re.cellfactory.editing.LongDatePicker;
import de.citec.csra.re.cellfactory.editing.StringTextField;
import de.citec.csra.re.cellfactory.editing.ValueCheckBox;
import de.citec.csra.re.struct.Leaf;
import de.citec.csra.re.struct.LeafContainer;
import de.citec.csra.re.struct.Node;
import de.citec.csra.re.util.SelectableLabel;
import java.text.DecimalFormat;
import java.util.Date;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import rst.homeautomation.state.ActivationStateType.ActivationState;

/**
 *
 * @author thuxohl
 */
public abstract class ValueCell extends RowCell {

    protected final Button applyButton, cancelButton;
    protected final HBox buttonLayout;
    protected LeafContainer leaf;

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public ValueCell() {
        super();
        applyButton = new Button("Apply");
        cancelButton = new Button("Cancel");
        buttonLayout = new HBox(applyButton, cancelButton);
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (getItem() instanceof Leaf && ((LeafContainer) getItem()).getEditable()) {
            leaf = ((LeafContainer) getItem());

            setGraphic(getEditingGraphic());
        }
    }

    private javafx.scene.Node getEditingGraphic() {
        javafx.scene.Node graphic = null;
        if (leaf.getValue() instanceof String) {
            graphic = new StringTextField(this, (String) leaf.getValue());
        } else if (leaf.getValue() instanceof Enum) {
            if (leaf.getParent().getBuilder() instanceof ActivationState.Builder) {
                graphic = new ValueCheckBox(this, ActivationState.State.ACTIVE, ActivationState.State.DEACTIVE);
            } else {
                graphic = new EnumComboBox(this, leaf.getValue().getClass());
            }
        } else if (leaf.getValue() instanceof Float || leaf.getValue() instanceof Double) {
            graphic = new DecimalTextField(this, leaf.getValue().toString());
        } else if (leaf.getValue() instanceof Long) {
            graphic = new LongDatePicker(this, (Long) leaf.getValue());
        } else if (leaf.getValue() instanceof Boolean) {
            graphic = new ValueCheckBox(this, true, false);
        }
        return graphic;
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setGraphic(null);
    }

    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        logger.info("updatig item");
        if (empty) {
            setGraphic(null);
            setText("");
            setContextMenu(null);
        } else if (item instanceof Leaf) {
            String text = "";
            if (((Leaf) item).getValue() instanceof Long) {
                text = LongDatePicker.DATE_CONVERTER.format(new Date((Long) ((Leaf) item).getValue()));
            } else if (((Leaf) item).getValue() instanceof Double) {
                text = decimalFormat.format(((Double) ((Leaf) item).getValue()));
            } else if ((((Leaf) item).getValue() != null)) {
                text = ((Leaf) item).getValue().toString();
            }
            setText(text);
//            setGraphic(new SelectableLabel(text));
            logger.info("Created selectable label");
        }
        logger.info("updatig item finished");

//        if (item instanceof DeviceClassContainer) {
//            setText(((DeviceClassContainer) item).getBuilder().getDescription());
//        } else if (item instanceof DeviceConfigContainer) {
//            setText(((DeviceConfigContainer) item).getBuilder().getDescription());
//        } else if (item instanceof UnitConfigContainer) {
//            setText(((UnitConfigContainer) item).getBuilder().getDescription());
//        } else if (item instanceof DeviceConfigGroupContainer) {
//            setText(((DeviceConfigGroupContainer) item).getDeviceClass().getDescription());
//        } else if (item instanceof EntryContainer) {
//            setText(((EntryContainer) item).getDescription());
//        }
    }

    public LeafContainer getLeaf() {
        return leaf;
    }

    public void commitEdit() {
        super.commitEdit(leaf);
    }
}
