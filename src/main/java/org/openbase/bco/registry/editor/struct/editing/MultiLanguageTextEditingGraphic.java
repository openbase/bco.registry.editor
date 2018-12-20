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

import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.HBox;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.LanguageComboBox;
import org.openbase.type.language.MultiLanguageTextType.MultiLanguageText;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class MultiLanguageTextEditingGraphic extends AbstractBuilderEditingGraphic<HBox, MultiLanguageText.MapFieldEntry.Builder> {

    private LanguageComboBox languageComboBox;
    private TextField textField;

    public MultiLanguageTextEditingGraphic(ValueType<MultiLanguageText.MapFieldEntry.Builder> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new HBox(), valueType, treeTableCell);

//        getControl().setOnKeyReleased(event -> {
//            switch (event.getCode()) {
//                case ESCAPE:
//                    treeTableCell.cancelEdit();
//                    break;
//                case ENTER:
//                    commitEdit();
//            }
//        });
    }

    @Override
    protected boolean updateBuilder(MultiLanguageText.MapFieldEntry.Builder builder) {
        if (builder.getKey().equals(languageComboBox.getValue())
                && builder.getValue().equals(textField.getText())) {
            return false;
        }

        if (languageComboBox.getValue() != null) {
            builder.setKey(languageComboBox.getValue());
        }

        builder.clearValue().setValue(textField.getText());
        return true;
    }

    @Override
    protected void init(final MultiLanguageText.MapFieldEntry.Builder value) {
        this.languageComboBox = new LanguageComboBox();
        this.textField = new TextField();
        //TODO: textfield should occupy all space not needed by the combo box in the cell
//        getControl().widthProperty().addListener((observable, oldValue, newValue) -> {
//            textField.setPrefWidth(newValue.doubleValue() - languageComboBox.getWidth());
//            System.out.println(languageComboBox.getWidth() + ", " + languageComboBox.getPrefWidth());
//        });
//        languageComboBox.widthProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                textField.setPrefWidth(getControl().getWidth() - newValue.doubleValue());
//                System.out.println("ComboBox: " + newValue.doubleValue() + ", " + getControl().getPrefWidth());
//            }
//        });
        getControl().getChildren().addAll(this.languageComboBox, this.textField);


        if (value.hasKey()) {
            languageComboBox.getSelectionModel().select(value.getKey());
        }
        textField.setText(value.getValue());
    }
}
