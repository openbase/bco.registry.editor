/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.struct.converter;

import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.extension.rsb.scope.ScopeGenerator;
import java.util.HashMap;
import java.util.Map;
import rst.rsb.ScopeType.Scope;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class ScopeConverter implements Converter {

    private final Scope.Builder scope;
    private static final String SCOPE = "scope";

    public ScopeConverter(Scope.Builder scope) {
        this.scope = scope;
    }

    @Override
    public void updateBuilder(String fieldName, Object value) throws CouldNotPerformException {
        try {
            switch (fieldName) {
                case SCOPE:
                    String[] split = ((String) value).split("/");
                    for (int i = 0; i < split.length; i++) {
                        scope.setComponent(i, ScopeGenerator.convertIntoValidScopeComponent(split[i]));
                    }
                    break;
                default:
            }
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not update rotation with [" + fieldName + "," + value + "]", ex);
        }

    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put(SCOPE, ScopeGenerator.generateStringRep(scope.getComponentList()));
        return fieldMap;
    }
}
