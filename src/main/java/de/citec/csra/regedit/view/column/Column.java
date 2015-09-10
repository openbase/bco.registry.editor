/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.column;

import de.citec.csra.regedit.struct.Node;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thuxohl
 */
public abstract class Column extends TreeTableColumn<Node, Node> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public Column(String text) {
        super(text);
        this.setCellValueFactory(new TreeItemPropertyValueFactory<>("context"));
    }

    public abstract void addWidthProperty(ReadOnlyDoubleProperty widthProperty);
}
