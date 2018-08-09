package org.openbase.bco.registry.editor.struct.editing.util;

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import javafx.scene.control.ListCell;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class EnumValueDescriptorCell extends ListCell<EnumValueDescriptor> {

    @Override
    public void updateItem(EnumValueDescriptor item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(item.getName());
        }
    }
}
