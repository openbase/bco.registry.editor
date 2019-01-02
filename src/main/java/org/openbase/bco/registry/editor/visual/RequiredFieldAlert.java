package org.openbase.bco.registry.editor.visual;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2019 openbase.org
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

import com.google.protobuf.Message;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;

import java.util.List;
import java.util.Optional;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RequiredFieldAlert extends Alert {

    public RequiredFieldAlert(final Message.Builder builder) {
        super(AlertType.CONFIRMATION);

        this.setResizable(true);
        this.setTitle("Message initialization error!");
        this.setHeaderText("Missing some required fields!");
        this.setContentText("Initialize them with default values or clear them?");

        ButtonType initButton = new ButtonType("Init");
        ButtonType clearButton = new ButtonType("Clear");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        this.getButtonTypes().setAll(initButton, clearButton, cancelButton);

        List<String> missingFieldList = builder.findInitializationErrors();
        String missingFields = "";
        missingFields = missingFieldList.stream().map((error) -> error + "\n").reduce(missingFields, String::concat);

        final TextArea textArea = new TextArea(missingFields);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        final Label label = new Label("Missing fields:");

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        final GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        this.getDialogPane().setExpandableContent(expContent);

        final Optional<ButtonType> result = this.showAndWait();
        if (result.isPresent() && result.get() == clearButton) {
            ProtoBufFieldProcessor.clearRequiredFields(builder);
        } else if (result.isPresent() && result.get() == initButton) {
            ProtoBufFieldProcessor.initRequiredFieldsWithDefault(builder);
        }
    }
}
