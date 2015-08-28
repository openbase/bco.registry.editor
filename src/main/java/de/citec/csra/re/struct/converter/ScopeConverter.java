/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.converter;

import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.extension.rsb.scope.ScopeGenerator;
import java.util.ArrayList;
import java.util.List;
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
    public List<ValueTupel> getFields() {
        List<ValueTupel> fields = new ArrayList<>();
        fields.add(new ValueTupel(ScopeGenerator.generateStringRep(scope.getComponentList()), SCOPE));
        return fields;
    }
}
