package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.FieldDescriptor;
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.LeafTreeItem;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.struct.editing.LocationIdEditingGraphic;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import rst.spatial.PlacementConfigType.PlacementConfig;
import rst.spatial.PlacementConfigType.PlacementConfig.Builder;

import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class PlacementConfigTreeItem extends BuilderTreeItem<PlacementConfig.Builder> {

    public PlacementConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        final Set<Integer> uneditableFields = super.getUneditableFields();
        uneditableFields.add(PlacementConfig.TRANSFORMATION_FRAME_ID_FIELD_NUMBER);
        return uneditableFields;
    }

    @Override
    protected GenericTreeItem createChild(final FieldDescriptor field, final Boolean editable) throws CouldNotPerformException {
        switch (field.getNumber()) {
            case PlacementConfig.LOCATION_ID_FIELD_NUMBER:
                final LeafTreeItem<String> leafTreeItem = new LeafTreeItem<>(field, getBuilder().getLocationId(), getBuilder(), editable);
                leafTreeItem.setDescriptionGenerator(value -> {
                    try {
                        return ScopeGenerator.generateStringRep(Registries.getUnitRegistry().getUnitConfigById(value).getScope());
                    } catch (CouldNotPerformException e) {
                        return value;
                    }
                });
                leafTreeItem.setEditingGraphicFactory(EditingGraphicFactory.getInstance(LocationIdEditingGraphic.class));
                return leafTreeItem;
            default:
                return super.createChild(field, editable);
        }
    }
}
