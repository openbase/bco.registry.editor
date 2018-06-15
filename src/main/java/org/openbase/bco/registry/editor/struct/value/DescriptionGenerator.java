package org.openbase.bco.registry.editor.struct.value;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public interface DescriptionGenerator<V> {

    String getValueDescription(final V value);

    String getDescription(final V value);
}
