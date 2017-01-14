package org.openbase.bco.registry.editor.struct.converter;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2017 openbase.org
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
import java.io.NotActiveException;
import java.util.HashMap;
import java.util.Map;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import rst.rsb.ScopeType.Scope;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ScopeConverter implements Converter {

    private final Scope.Builder scope;
    private static final String SCOPE = "scope";

    public ScopeConverter(Scope.Builder scope) {
        this.scope = scope;
    }

    @Override
    public void updateBuilder(String fieldName, Object value) throws CouldNotPerformException {
        switch (fieldName) {
            case SCOPE:
                String[] split = ((String) value).split("/");
                for (int i = 0; i < split.length; i++) {
                    scope.setComponent(i, ScopeGenerator.convertIntoValidScopeComponent(split[i]));
                }
                break;
            default:
        }
    }

    @Override
    public Map<String, Object> getFields() throws NotAvailableException {
        try {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put(SCOPE, ScopeGenerator.generateStringRep(scope.getComponentList()));
            return fieldMap;
        } catch (CouldNotPerformException ex) {
            throw new NotAvailableException("scope fields", ex);
        }
    }
}
