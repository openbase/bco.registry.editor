/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.converter;

import com.google.protobuf.GeneratedMessage;
import rst.geometry.RotationType.Rotation;
import rst.rsb.ScopeType.Scope;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class ConverterSelector {

    public static Converter getConverter(GeneratedMessage.Builder builder) {
        if (builder instanceof Scope.Builder) {
            return new ScopeConverter((Scope.Builder) builder);
        } else if (builder instanceof Rotation.Builder) {
            return new QuaternionEulerConverter((Rotation.Builder) builder);
        } else {
            return null;
        }
    }
}
