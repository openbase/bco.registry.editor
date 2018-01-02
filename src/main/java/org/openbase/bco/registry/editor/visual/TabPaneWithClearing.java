/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openbase.bco.registry.editor.visual;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import java.util.HashMap;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import org.openbase.bco.registry.editor.util.SendableType;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class TabPaneWithClearing extends TabPane {
    
    private final Map<SendableType, Tab> senableToTabMap;
    
    public TabPaneWithClearing() {
        super();
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent event) {
                GlobalTextArea.getInstance().clearText();
            }
        });
        senableToTabMap = new HashMap<>();
    }
    
    public void addTab(Tab tab, SendableType sendableType) {
        senableToTabMap.put(sendableType, tab);
        getTabs().add(tab);
    }
    
    public void selectTabByType(SendableType sendableType) {
        this.getSelectionModel().select(senableToTabMap.get(sendableType));
    }
}
