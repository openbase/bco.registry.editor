/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re;

import de.citec.jul.exception.printer.ExceptionPrinter;
import javafx.scene.control.TextArea;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class GlobalTextArea extends TextArea {
    
    private static GlobalTextArea instance;
    
    private String readOnly;
    
    public static GlobalTextArea getInstance() {
        if (instance == null) {
            instance = new GlobalTextArea();
        }
        return instance;
    }
    
    public GlobalTextArea() {
        super();
        setEditable(false);
    }
    
    public void addReadOnlyMessage(String msg) {
        
    } 
}
