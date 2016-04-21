/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.registry.editor.visual;

import javafx.event.EventHandler;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class TabPaneWithClearing extends TabPane {

    public TabPaneWithClearing() {
        super();
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                GlobalTextArea.getInstance().clearText();
            }
        });
    }
}
