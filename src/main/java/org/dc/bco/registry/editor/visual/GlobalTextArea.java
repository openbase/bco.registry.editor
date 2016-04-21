package org.dc.bco.registry.editor.visual;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 DivineCooperation
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import org.dc.jul.exception.printer.ExceptionPrinter;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class GlobalTextArea extends StackPane {

    private final TextArea textArea;
    private static GlobalTextArea globalTextArea;

    public static GlobalTextArea getInstance() {
        if (globalTextArea == null) {
            globalTextArea = new GlobalTextArea();
        }
        return globalTextArea;
    }

    public GlobalTextArea() {
        this.textArea = new TextArea();
        this.textArea.setEditable(false);
        this.textArea.setFont(Font.font("Monospaced"));
        BorderPane borderPane1 = new BorderPane();
        BorderPane borderPane2 = new BorderPane();
        borderPane1.setBottom(borderPane2);
        Button button = new Button("Clear");
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                textArea.clear();
            }
        });
        borderPane2.setRight(button);
        this.getChildren().addAll(textArea, borderPane1);
    }

    public void printException(Throwable th) {
        this.textArea.clear();
        this.textArea.setText(ExceptionPrinter.getHistory(th));
    }

    public void clearText() {
        this.textArea.clear();
    }

    public void setText(String text) {
        this.textArea.setText(text);
    }

    public void setTextAreaStyle(String style) {
        this.textArea.setStyle(style);
    }
}
