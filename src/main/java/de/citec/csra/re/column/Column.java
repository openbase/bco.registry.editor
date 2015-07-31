/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.column;

import de.citec.csra.re.struct.Node;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

/**
 *
 * @author thuxohl
 */
public abstract class Column extends TreeTableColumn<Node, Node> {

    public static final int COLUMN_WIDTH = 400;

    public Column(String text) {
        super(text);
        this.setCellValueFactory(new TreeItemPropertyValueFactory<>("context"));
    }
}
