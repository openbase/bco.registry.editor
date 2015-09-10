/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.cell.editing;

import de.citec.csra.regedit.view.cell.ValueCell;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class ValueCheckBox extends CheckBox {

    private final Object selected;
    private final Object unselected;
    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    public ValueCheckBox(ValueCell cell) {
        this(cell, true, false);
    }

    public ValueCheckBox(ValueCell cell, Object selected, Object unselected) {
        super();
        this.selected = selected;
        this.unselected = unselected;
        setSelected(cell.getLeaf().getValue().equals(selected));
        setVisible(true);
        setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                Object value;
                if (isSelected()) {
                    value = selected;
                } else {
                    value = unselected;
                }
                cell.getLeaf().setValue(value);
                cell.setText(value.toString());
                cell.commitEdit(cell.getLeaf());
            }
        });
    }
}
