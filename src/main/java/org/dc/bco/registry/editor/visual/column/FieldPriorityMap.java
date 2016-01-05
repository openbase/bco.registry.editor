/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.registry.editor.visual.column;

import java.util.HashMap;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class FieldPriorityMap extends HashMap<String, Integer> {

    public FieldPriorityMap() {
        this.put("id", -1000);
        this.put("label", -500);
        this.put("serial_number", -700);
        this.put("product_number", -700);
        this.put("device_class_id", -400);
    }
}
