/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.cell.editing;

import de.citec.csra.regedit.view.cell.ValueCell;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class StringTextField extends TextField {

    public StringTextField(ValueCell cell, String text) {
        super();
        setText(text);
        focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue && !cell.getLeaf().getValue().equals(getText())) {
                    cell.getLeaf().setValue(getText());
                    // even though commit is called the text property won't change fast enough without this line?!?
                    cell.setText(getText());
                    cell.commitEdit(cell.getLeaf());
                }
            }
        });
        setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ESCAPE)) {
                    cell.cancelEdit();
                } else if (event.getCode().equals(KeyCode.ENTER)) {
                    cell.getLeaf().setValue(getText());
                    cell.setText(getText());
                    cell.commitEdit(cell.getLeaf());
                }
            }
        });
    }
}
