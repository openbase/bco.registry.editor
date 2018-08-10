package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.FieldDescriptor;
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.jul.exception.InitializationException;
import rst.domotic.unit.location.LocationConfigType.LocationConfig;
import rst.domotic.unit.location.LocationConfigType.LocationConfig.Builder;

import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LocationConfigTreeItem extends BuilderTreeItem<LocationConfig.Builder> {

    public LocationConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        final Set<Integer> uneditableFields = super.getUneditableFields();
        uneditableFields.add(LocationConfig.UNIT_ID_FIELD_NUMBER);
        return uneditableFields;
    }
}
