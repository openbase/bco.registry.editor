package org.openbase.bco.registry.editor.util;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public interface DescriptionGenerator<V> {

    String getDescription(V value);
}
