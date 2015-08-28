/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory.editing;

import de.citec.csra.re.cellfactory.ValueCell;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class ValueCheckBox extends CheckBox {

    private final Object selected;
    private final Object unselected;
    
    public ValueCheckBox(ValueCell cell) {
        this(cell, true, false);
    }

    public ValueCheckBox(ValueCell cell, Object selected, Object unselected) {
        super();
        this.selected = selected;
        this.unselected = unselected;
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
