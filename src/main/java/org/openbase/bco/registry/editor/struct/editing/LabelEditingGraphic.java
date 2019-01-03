package org.openbase.bco.registry.editor.struct.editing;

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

import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.HBox;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.LanguageComboBox;
import org.openbase.type.language.LabelType.Label.MapFieldEntry.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LabelEditingGraphic extends AbstractBuilderEditingGraphic<HBox, Builder> {

    private LanguageComboBox languageComboBox;
    private TextField labelTextField;

    public LabelEditingGraphic(ValueType<Builder> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new HBox(), valueType, treeTableCell);
    }

    @Override
    protected boolean updateBuilder(Builder builder) {
        final String newKey = languageComboBox.getValue();
        final List<String> newLabelList = new ArrayList<>();
        for (final String label : labelTextField.getText().split(",")) {
            newLabelList.add(label.trim());
        }

        if (builder.getKey().equals(newKey) && equalLabelList(newLabelList, builder.getValueList())) {
            return false;
        }

        if(newKey != null) {
            builder.setKey(newKey);
        }
        
        builder.clearValue().addAllValue(newLabelList);
        return true;
    }

    private boolean equalLabelList(final List<String> list1, final List<String> list2) {
        for (final String label : list1) {
            if (!list2.contains(label)) {
                return false;
            }
        }
        for (final String label : list2) {
            if (!list1.contains(label)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void init(final Builder value) {
        this.languageComboBox = new LanguageComboBox();
        this.labelTextField = new TextField();
        //TODO: textfield should occupy all space not needed by the combo box in the cell
//        getControl().widthProperty().addListener((observable, oldValue, newValue) -> {
//            labelTextField.setPrefWidth(newValue.doubleValue() - languageComboBox.getWidth());
//            System.out.println(languageComboBox.getWidth() + ", " + languageComboBox.getPrefWidth());
//        });
//        languageComboBox.widthProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                labelTextField.setPrefWidth(getControl().getWidth() - newValue.doubleValue());
//                System.out.println("ComboBox: " + newValue.doubleValue() + ", " + getControl().getPrefWidth());
//            }
//        });
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
}
