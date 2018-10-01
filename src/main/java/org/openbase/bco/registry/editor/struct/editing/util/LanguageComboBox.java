package org.openbase.bco.registry.editor.struct.editing.util;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LanguageComboBox extends ScrollingComboBox<String> {

    private static final String LANGUAGE_TECHNICAL = "Technical";
    private static final String LANGUAGE_CODE_TECHNICAL = "technical";

    public LanguageComboBox() {
        super(LanguageComboBox::getDisplayedTest);

        this.setItems(createSortedLanguageCodeList());
    }

    private ObservableList<String> createSortedLanguageCodeList() {
        final List<String> languageCodeList = new ArrayList<>(Arrays.asList(Locale.getISOLanguages()));
        languageCodeList.add(LANGUAGE_CODE_TECHNICAL);
        languageCodeList.sort((languageCode1, languageCode2) -> {
            final String displayed1 = getDisplayedTest(languageCode1);
            final String displayed2 = getDisplayedTest(languageCode2);
            return displayed1.compareTo(displayed2);
        });
        return FXCollections.observableArrayList(languageCodeList);
    }

    public static String getDisplayedTest(final String languageCode) {
        if (languageCode.equals(LANGUAGE_CODE_TECHNICAL)) {
            return LANGUAGE_TECHNICAL;
        } else {
            return new Locale(languageCode).getDisplayLanguage();
        }
    }
}
