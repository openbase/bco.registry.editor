/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.registry.editor.util;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class SelectableLabel {

    public static Label makeSelectable(Label label) {
        StackPane textStack = new StackPane();
        TextField textField = new TextField(label.getText());
        textField.setEditable(false);
        textField.setStyle(
                "-fx-background-color: transparent; -fx-background-insets: 0; -fx-background-radius: 0; -fx-padding: 0; -fx-text-inner-color: gray;"
        );
        // the invisible label is a hack to get the textField to size like a label.
        Label invisibleLabel = new Label();
        invisibleLabel.textProperty().bind(label.textProperty());
        invisibleLabel.setVisible(false);
        textStack.getChildren().addAll(invisibleLabel, textField);
        label.textProperty().bindBidirectional(textField.textProperty());
        label.setGraphic(textStack);
        label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return label;
    }
}
