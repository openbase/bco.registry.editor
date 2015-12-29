/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view;

import org.dc.jul.exception.printer.ExceptionPrinter;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class GlobalTextArea extends TextArea {
    
    private static GlobalTextArea globalTextArea;
    
    public static GlobalTextArea getInstance() {
        if (globalTextArea == null) {
            globalTextArea = new GlobalTextArea();
        }
        return globalTextArea;
    }
    
    public GlobalTextArea() {
        this.setEditable(false);
        this.setFont(Font.font("Monospaced"));
    }
    
    public void printException(Throwable th) {
        this.setText("");
        this.setText(ExceptionPrinter.getHistory(th));
    }
}
