package org.openbase.bco.registry.editor.util;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DefaultDescriptionGenerator<V> implements DescriptionGenerator<V> {

    @Override
    public String getDescription(V value) {
        return value.toString();
    }
}
