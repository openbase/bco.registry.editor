package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.processing.StringProcessor;
import rst.domotic.service.ServiceTemplateConfigType.ServiceTemplateConfig;
import rst.domotic.service.ServiceTemplateConfigType.ServiceTemplateConfig.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ServiceTemplateConfigTreeItem extends BuilderTreeItem<ServiceTemplateConfig.Builder> {

    public ServiceTemplateConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @Override
    protected Node createDescriptionGraphic() {
        return new Label(StringProcessor.transformToCamelCase(getBuilder().getServiceType().name()));
    }
}
