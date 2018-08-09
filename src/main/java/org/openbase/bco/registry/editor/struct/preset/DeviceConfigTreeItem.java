package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.LeafTreeItem;
import org.openbase.bco.registry.editor.struct.editing.DeviceClassIdEditingGraphic;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import rst.domotic.unit.device.DeviceConfigType.DeviceConfig;
import rst.domotic.unit.device.DeviceConfigType.DeviceConfig.Builder;

import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class DeviceConfigTreeItem extends BuilderTreeItem<DeviceConfig.Builder> {

    public DeviceConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @Override
    protected GenericTreeItem createChild(FieldDescriptor field, Boolean editable) throws CouldNotPerformException {
        GenericTreeItem child;
        //TODO fix, also adding device class units does not set the device type automatically -> have a look at default instances
        if (field.getNumber() == DeviceConfig.DEVICE_CLASS_ID_FIELD_NUMBER) {
            LeafTreeItem leaf = new LeafTreeItem<String>(field, getBuilder().getDeviceClassId(), getBuilder(), editable) {
                @Override
                protected Node createValueGraphic() {
                    return new Label(this.getValueCasted().getValue());
                }
            };
            leaf.setEditingGraphicFactory(EditingGraphicFactory.getInstance(DeviceClassIdEditingGraphic.class));
            child = leaf;
        } else {
            child = super.createChild(field, editable);
        }
        return child;
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        Set<Integer> uneditableFields = super.getUneditableFields();
        uneditableFields.add(DeviceConfig.UNIT_ID_FIELD_NUMBER);
//        if (!getBuilder().getDeviceClassId().isEmpty()) {
//            uneditableFields.add(DeviceConfig.DEVICE_CLASS_ID_FIELD_NUMBER);
//        }
        return uneditableFields;
    }
}
