/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view;

import de.citec.csra.regedit.struct.Node;
import de.citec.csra.regedit.util.SendableType;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class VBoxWithLabelAndTreeTable extends VBox {
    
    private static final Logger logger = LoggerFactory.getLogger(VBoxWithLabelAndTreeTable.class);
    
    private final Label readOnlyLabel;
    private final RegistryTreeTableView registryTreeTableView;
    private final SendableType type;
    
    public VBoxWithLabelAndTreeTable(SendableType type) {
        this.type = type;
        
        this.setAlignment(Pos.CENTER);
        readOnlyLabel = new Label("Read-Only-Mode");
        readOnlyLabel.setAlignment(Pos.CENTER);
        readOnlyLabel.setStyle("-fx-text-background-color: rgb(255,128,0); -fx-font-weight: bold;");
        
        registryTreeTableView = new RegistryTreeTableView(type);
        registryTreeTableView.setContextMenu(new TreeTableViewContextMenu(registryTreeTableView, type));
        
        this.getChildren().addAll(registryTreeTableView);
    }
    
    public void setEditable(boolean editable) {
        if (!editable) {
            this.getChildren().clear();
            this.getChildren().addAll(readOnlyLabel, registryTreeTableView);
        } else {
            this.getChildren().remove(readOnlyLabel);
        }
        registryTreeTableView.setEditable(editable);
    }
    
    public Label getReadOnlyLabel() {
        return readOnlyLabel;
    }
    
    public RegistryTreeTableView getRegistryTreeTableView() {
        return registryTreeTableView;
    }
    
    public void setRoot(TreeItem<Node> value) {
        registryTreeTableView.setRoot(value);
    }
    
    public void addWidthProperty(ReadOnlyDoubleProperty widthProperty) {
        registryTreeTableView.addWidthProperty(widthProperty);
    }
    
    public void addHeightProperty(ReadOnlyDoubleProperty heightProperty) {
        heightProperty.addListener(new ChangeListener<Number>() {
            
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                registryTreeTableView.setPrefHeight(newValue.doubleValue());
            }
        });
    }
    
    public void setContextMenu(boolean visible) {
        if (visible) {
            logger.info("added context menu to [" + type + "]");
            registryTreeTableView.setContextMenu(new TreeTableViewContextMenu(registryTreeTableView, type));
        } else {
            logger.info("removed context menu to [" + type + "]");
            registryTreeTableView.setContextMenu(null);
        }
    }
}
