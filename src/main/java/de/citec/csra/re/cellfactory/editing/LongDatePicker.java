/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory.editing;

import de.citec.csra.re.cellfactory.ValueCell;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.DatePicker;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class LongDatePicker extends DatePicker {

    public static final DateFormat DATE_CONVERTER = new SimpleDateFormat("dd/MM/yyyy");

    public LongDatePicker(ValueCell cell, Long time) {
        super();
        Date date = new Date(time);
        setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (getValue() != null && getValue().toEpochDay() != (Long) cell.getLeaf().getValue()) {
                    cell.getLeaf().setValue(getValue().toEpochDay() * 24 * 60 * 60 * 1000);
                    cell.setText(DATE_CONVERTER.format(new Date((Long) cell.getLeaf().getValue())));
                    cell.commitEdit(cell.getLeaf());
                }
            }
        });
    }
}
