package org.openbase.bco.registry.editor.struct.preset;

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.openbase.bco.registry.editor.struct.BuilderListTreeItem;
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import rst.domotic.service.ServiceDescriptionType.ServiceDescription;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import rst.domotic.unit.UnitTemplateConfigType.UnitTemplateConfig;
import rst.domotic.unit.UnitTemplateConfigType.UnitTemplateConfig.Builder;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class UnitTemplateConfigTreeItem extends BuilderTreeItem<UnitTemplateConfig.Builder> {

    public UnitTemplateConfigTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        addEventHandler(valueChangedEvent(), event -> {
            updateDescriptionGraphic();
            final GenericTreeItem source = (GenericTreeItem) event.getSource();
            if (source.getFieldDescriptor().getNumber() == UnitTemplateConfig.TYPE_FIELD_NUMBER) {
                // try to find position of service template tree item
                int index = -1;
                for (int i = 0; i < getChildren().size(); i++) {
                    if (((GenericTreeItem) getChildren().get(i)).getFieldDescriptor().getNumber() == UnitTemplateConfig.SERVICE_TEMPLATE_CONFIG_FIELD_NUMBER) {
                        index = i;
                        break;
                    }
                }

                try {
                    final UnitType newValue = UnitType.valueOf(((ValueType<EnumValueDescriptor>) event.getNewValue()).getValue().getNumber());

                    // filter so that every serviceType is only added once
                    final Set<ServiceType> serviceTypeSet = new HashSet<>();
                    for (final ServiceDescription serviceDescription : Registries.getTemplateRegistry().getUnitTemplateByType(newValue).getServiceDescriptionList()) {
                        serviceTypeSet.add(serviceDescription.getServiceType());
                    }

                    // retrieve service template config field
                    FieldDescriptor field = ProtoBufFieldProcessor.getFieldDescriptor(getBuilder(), UnitTemplateConfig.SERVICE_TEMPLATE_CONFIG_FIELD_NUMBER);

                    // clear current service template
                    getBuilder().clearField(field);

                    // add new service template configs
                    for (ServiceType serviceType : serviceTypeSet) {
                        getBuilder().addServiceTemplateConfigBuilder().setServiceType(serviceType);
                    }

                    // create tree item, init its children
                    final BuilderListTreeItem<Builder> treeItem = new BuilderListTreeItem<>(field, getBuilder(), false);
                    treeItem.getChildren();
                    treeItem.setExpanded(true);
                    // if builder position was found previously exchange tree item, else add
                    if (index != -1) {
                        getChildren().set(index, treeItem);
                    } else {
                        getChildren().add(treeItem);
                    }
                } catch (CouldNotPerformException ex) {
                    logger.warn("Could not update service template configs", ex);
                }
            }
        });
    }

    @Override
    protected Set<Integer> getUneditableFields() {
        final Set<Integer> uneditableFields = super.getUneditableFields();
        uneditableFields.add(UnitTemplateConfig.ID_FIELD_NUMBER);
        uneditableFields.add(UnitTemplateConfig.SERVICE_TEMPLATE_CONFIG_FIELD_NUMBER);
        return uneditableFields;
    }

    @Override
    protected Node createDescriptionGraphic() {
        try {
            return new Label(LabelProcessor.getBestMatch(getBuilder().getLabel()));
        } catch (NotAvailableException ex) {
            return super.createDescriptionGraphic();
        }
    }
}
