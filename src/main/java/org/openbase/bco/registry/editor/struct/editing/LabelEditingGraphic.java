package org.openbase.bco.registry.editor.struct.editing;

/*-
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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.HBox;
import org.openbase.bco.registry.editor.struct.ValueType;
import rst.configuration.LabelType.Label.MapFieldEntry.Builder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LabelEditingGraphic extends AbstractBuilderEditingGraphic<HBox, Builder> {

    private ComboBox<String> languageComboBox;
    private TextField labelTextField;

    public LabelEditingGraphic(ValueType<Builder> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new HBox(), valueType, treeTableCell);

        getControl().setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    treeTableCell.cancelEdit();
                    break;
                case ENTER:
                    commitEdit();
            }
        });
    }

    @Override
    protected void updateBuilder(Builder builder) {
        builder.setKey(languageComboBox.getValue());
        builder.clearValue();
        for (final String label : labelTextField.getText().split(",")) {
            builder.addValue(label.trim());
        }
    }

    @Override
    protected void init(final Builder value) {
        this.languageComboBox = new ComboBox<>();
        this.languageComboBox.setVisibleRowCount(10);
        this.languageComboBox.setCellFactory(param -> new LanguageCell());
        this.languageComboBox.setButtonCell(new LanguageCell());
        this.languageComboBox.setItems(createSortedLanguageCodeList());

        this.labelTextField = new TextField();
        getControl().getChildren().addAll(this.languageComboBox, this.labelTextField);


        if (value.hasKey()) {
            languageComboBox.getSelectionModel().select(value.getKey());
        }
        String labelText = "";
        for (int i = 0; i < value.getValueCount(); i++) {
            labelText += value.getValue(i);
            if (i < value.getValueCount() - 1) {
                labelText += ", ";
            }
        }
        labelTextField.setText(labelText);
    }

    private ObservableList<String> createSortedLanguageCodeList() {
        final List<String> languageCodeList = Arrays.asList(Locale.getISOLanguages());
        languageCodeList.sort((languageCode1, languageCode2) -> {
            final String displayed1 = new Locale(languageCode1).getDisplayLanguage();
            final String displayed2 = new Locale(languageCode2).getDisplayLanguage();
            return displayed1.compareTo(displayed2);
        });
        return FXCollections.observableArrayList(languageCodeList);
    }

    private class LanguageCell extends ListCell<String> {

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(new Locale(item).getDisplayLanguage());
            }
        }
    }

}
