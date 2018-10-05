package org.openbase.bco.registry.editor.struct.preset;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.LeafTreeItem;
import org.openbase.bco.registry.editor.struct.editing.*;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.protobuf.processing.ProtoBufJSonProcessor;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import rst.domotic.service.ServiceDescriptionType.ServiceDescription;
import rst.domotic.service.ServiceStateDescriptionType.ServiceStateDescription;
import rst.domotic.service.ServiceStateDescriptionType.ServiceStateDescription.Builder;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate.ServicePattern;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import rst.domotic.state.ActivationStateType.ActivationState;
import rst.domotic.state.BrightnessStateType.BrightnessState;
import rst.domotic.state.ColorStateType.ColorState;
import rst.domotic.state.PowerStateType.PowerState;
import rst.domotic.state.StandbyStateType.StandbyState;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import rst.vision.HSBColorType.HSBColor;

import java.text.DecimalFormat;
import java.util.List;

/**
 * A tree item specialized in displaying service state descriptions.
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ServiceStateDescriptionTreeItem extends BuilderTreeItem<ServiceStateDescription.Builder> {

    /**
     * {@inheritDoc}
     *
     * @param fieldDescriptor {@inheritDoc}
     * @param builder         {@inheritDoc}
     * @param editable        {@inheritDoc}
     *
     * @throws InitializationException {@inheritDoc}
     */
    public ServiceStateDescriptionTreeItem(final FieldDescriptor fieldDescriptor, final Builder builder, final Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        // add an event handler to keep values consistent
        addEventHandler(valueChangedEvent(), event -> {
            final GenericTreeItem source = (GenericTreeItem) event.getSource();

            try {
                switch (source.getFieldDescriptor().getNumber()) {
                    case ServiceStateDescription.SERVICE_TYPE_FIELD_NUMBER:
                        handleServiceTypeUpdate();
                        break;
                    case ServiceStateDescription.UNIT_TYPE_FIELD_NUMBER:
                        handleUnitTypeUpdate();
                        break;
                    case ServiceStateDescription.SERVICE_ATTRIBUTE_FIELD_NUMBER:
                        handleServiceAttributeUpdate();
                        break;
                }
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory("Could not handle update to service state description", ex, logger);
            }
        });
    }

    /**
     * Handle changes in the service type of the service state description by updating the service attribute
     * and service attribute type.
     *
     * @throws CouldNotPerformException if the update could not be performed
     */
    @SuppressWarnings("unchecked")
    private void handleServiceTypeUpdate() throws CouldNotPerformException {
        // retrieve descriptors
        final FieldDescriptor attributeField = ServiceStateDescription.getDescriptor().findFieldByNumber(ServiceStateDescription.SERVICE_ATTRIBUTE_FIELD_NUMBER);
        final FieldDescriptor attributeTypeField = ServiceStateDescription.getDescriptor().findFieldByNumber(ServiceStateDescription.SERVICE_ATTRIBUTE_TYPE_FIELD_NUMBER);

        // retrieve tree items belonging to those descriptors
        final LeafTreeItem<String> attributeLeaf = (LeafTreeItem<String>) getDescriptorChildMap().get(attributeField);
        final LeafTreeItem<String> attributeTypeLeaf = (LeafTreeItem<String>) getDescriptorChildMap().get(attributeTypeField);

        if (getBuilder().getServiceType() != ServiceType.UNKNOWN) {
            // if service type is set and not unknown the attribute becomes editable and the attribute type is updated
            // note: the order here is important, if the attribute type is not updated before the attribute parsing the empty json value will fail
            attributeTypeLeaf.update(Registries.getTemplateRegistry().getServiceAttributeType(getBuilder().getServiceType()));
            attributeLeaf.setEditable(true);
            // update value with empty json object
            attributeLeaf.update("{}");
        } else {
            // service type is not set or unknown so the attribute is not editable and the type is cleared
            attributeLeaf.setEditable(false);
            attributeLeaf.update("");
            attributeTypeLeaf.update("");
        }
    }

    /**
     * Handle changes in the unit type of the service state description by updating the unit id and service type.
     *
     * @throws CouldNotPerformException if the update could not be performed
     */
    @SuppressWarnings("unchecked")
    private void handleUnitTypeUpdate() throws CouldNotPerformException {
        // retrieve descriptors
        final FieldDescriptor serviceTypeField = ServiceStateDescription.getDescriptor().findFieldByNumber(ServiceStateDescription.SERVICE_TYPE_FIELD_NUMBER);
        final FieldDescriptor unitIdField = ServiceStateDescription.getDescriptor().findFieldByNumber(ServiceStateDescription.UNIT_ID_FIELD_NUMBER);

        // retrieve tree items belonging to those descriptors
        LeafTreeItem<String> unitIdLeaf = (LeafTreeItem<String>) getDescriptorChildMap().get(unitIdField);
        LeafTreeItem<EnumValueDescriptor> serviceTypeLeaf = (LeafTreeItem<EnumValueDescriptor>) getDescriptorChildMap().get(serviceTypeField);

        // unit id always becomes editable because the unit type is now set
        unitIdLeaf.setEditable(true);
        if (!getBuilder().getUnitId().isEmpty()) {
            // if the unit id was set verify that the unit still belongs to the new unit type or is a sub unit type
            final UnitType unitType = Registries.getUnitRegistry().getUnitConfigById(getBuilder().getUnitId()).getUnitType();
            final List<UnitType> subUnitTypes = Registries.getTemplateRegistry().getSubUnitTypes(getBuilder().getUnitType());
            if (getBuilder().getUnitType() != unitType && !subUnitTypes.contains(unitType)) {
                unitIdLeaf.update("");
            }
        } else {
            // if no unit was previously defined it can happen that the unit type was previously unknown
            // therefore the leaf was not editable before and has to be updated so that the GUI reflects this change
            unitIdLeaf.update("");
        }

        // service type always becomes editable because the unit type is now set
        serviceTypeLeaf.setEditable(true);
        // test if the current service type is still valid for the new unit type
        boolean contains = false;
        // iterate over all services of the new unit type
        for (ServiceDescription serviceDescription : Registries.getTemplateRegistry().getUnitTemplateByType(getBuilder().getUnitType()).getServiceDescriptionList()) {
            // skip everything that is not an operation service
            if (serviceDescription.getPattern() != ServicePattern.OPERATION) {
                continue;
            }

            // if the service type matches the current service type it is still contained in the new unit type so break out
            if (serviceDescription.getServiceType() == getBuilder().getServiceType()) {
                contains = true;
                break;
            }
        }

        // if the old service type is no longer contained set it to unknown
        if (!contains) {
            serviceTypeLeaf.update(ServiceType.UNKNOWN.getValueDescriptor());
        }
    }

    /**
     * Handle changes in the service attribute by creating a nicer value graphic than just the serialized text.
     */
    @SuppressWarnings("unchecked")
    private void handleServiceAttributeUpdate() {
        // do nothing if the service attribute has been cleared
        if (getBuilder().getServiceAttribute().isEmpty()) {
            return;
        }

        // retrieve service attribute leaf
        final FieldDescriptor attributeField = ServiceStateDescription.getDescriptor().findFieldByNumber(ServiceStateDescription.SERVICE_ATTRIBUTE_FIELD_NUMBER);
        final LeafTreeItem<String> attributeLeaf = (LeafTreeItem<String>) getDescriptorChildMap().get(attributeField);
        // create and set mew value graphic
        attributeLeaf.setValueGraphic(getServiceAttributeValueGraphic());
    }

    /**
     * Create a nicer value graphic representation for the service attribute. This is done by de-serializing it
     * and creating a description graphic from the service state.
     * If the value cannot be de-serialized or no nicer representation for the service state is available a default
     * label with the service attribute string is returned.
     *
     * @return a value graphic for the service attribute
     */
    private Node getServiceAttributeValueGraphic() {
        if (!getBuilder().hasServiceAttribute() || getBuilder().getServiceAttribute().isEmpty()) {
            return new Label();
        }
        try {
            // deserialize service attribute
            final Message serviceAttribute = new ProtoBufJSonProcessor().deserialize(getBuilder().getServiceAttribute(), getBuilder().getServiceAttributeType());
            switch (getBuilder().getServiceType()) {
                case COLOR_STATE_SERVICE:
                    // build a java fx color from the color state
                    final HSBColor hsbColor = ((ColorState) serviceAttribute).getColor().getHsbColor();
                    final Color hsb = Color.hsb(hsbColor.getHue(), hsbColor.getSaturation(), hsbColor.getBrightness());
                    // create a rectangle of that color and set it as the value graphic
                    final Rectangle rectangle = new Rectangle(15, 15);
                    rectangle.setFill(hsb);
                    return rectangle;
                case ACTIVATION_STATE_SERVICE:
                    // for an activation state display the enum value
                    final ActivationState.State activation = ((ActivationState) serviceAttribute).getValue();
                    return new Label(activation.name());
                case POWER_STATE_SERVICE:
                    // for a power state display the enum value
                    final PowerState.State power = ((PowerState) serviceAttribute).getValue();
                    return new Label(power.name());
                case STANDBY_STATE_SERVICE:
                    // for a standby state display the enum value
                    final StandbyState.State standby = ((StandbyState) serviceAttribute).getValue();
                    return new Label(standby.name());
                case BRIGHTNESS_STATE_SERVICE:
                    // for a brightness state format and display the brightness value
                    final DecimalFormat decimalFormat = new DecimalFormat("##.##");
                    final double brightness = ((BrightnessState) serviceAttribute).getBrightness();
                    return new Label(decimalFormat.format(brightness));
                default:
                    // show to the user that this service is currently not supported, meaning it cannot be edited
                    return new Label("Uneditable Attribute: " + getBuilder().getServiceAttribute());
            }
        } catch (CouldNotPerformException ex) {
            // log the exception and return a default value graphic
            ExceptionPrinter.printHistory("Could not generate value graphic for service attribute with type[" + getBuilder().getServiceType().name() + "]", ex, logger);
            return new Label(getBuilder().getServiceAttribute());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param field    {@inheritDoc}
     * @param editable {@inheritDoc}
     *
     * @return {@inheritDoc}
     *
     * @throws CouldNotPerformException {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected GenericTreeItem createChild(final FieldDescriptor field, final Boolean editable) throws CouldNotPerformException {
        switch (field.getNumber()) {
            case ServiceStateDescription.UNIT_ID_FIELD_NUMBER:
                // unit id is only editable if the unit type is set, gets a description generator displaying the scope
                // and gets a description graphic filtering units matching the setting of this service state description
                final LeafTreeItem<String> unitIdLeaf = new LeafTreeItem<>(field, getBuilder().getUnitId(), getBuilder().getUnitType() != UnitType.UNKNOWN);
                unitIdLeaf.setEditingGraphicFactory(EditingGraphicFactory.getInstance(ServiceStateUnitIdEditingGraphic.class));
                unitIdLeaf.setDescriptionGenerator(unitId -> {
                    try {
                        return ScopeGenerator.generateStringRep(Registries.getUnitRegistry().getUnitConfigById(unitId).getScope());
                    } catch (CouldNotPerformException e) {
                        return unitId;
                    }
                });
                return unitIdLeaf;
            case ServiceStateDescription.SERVICE_ATTRIBUTE_TYPE_FIELD_NUMBER:
                // service attribute type is derived from the service type and thus uneditable
                return super.createChild(field, false);
            case ServiceStateDescription.SERVICE_ATTRIBUTE_FIELD_NUMBER:
                // service attribute is only editable if the service type is set, its value graphic is created and
                // it received a special editing graphic
                LeafTreeItem<String> serviceAttributeLeaf = new LeafTreeItem<>(field, getBuilder().getServiceAttribute(), getBuilder().getServiceType() != ServiceType.UNKNOWN);
                serviceAttributeLeaf.setEditingGraphicFactory(EditingGraphicFactory.getInstance(ServiceStateAttributeEditingGraphic.class));
                serviceAttributeLeaf.setValueGraphic(getServiceAttributeValueGraphic());
                return serviceAttributeLeaf;
            case ServiceStateDescription.SERVICE_TYPE_FIELD_NUMBER:
                // service type is only editable if the unit type is set and gets a special editing graphic only
                // displaying operation services according to the unit type
                final LeafTreeItem serviceTypeLeaf = (LeafTreeItem) super.createChild(field, getBuilder().getUnitType() != UnitType.UNKNOWN);
                serviceTypeLeaf.setEditingGraphicFactory(EditingGraphicFactory.getInstance(ServiceStateServiceTypeEditingGraphic.class));
                return serviceTypeLeaf;
            case ServiceStateDescription.UNIT_TYPE_FIELD_NUMBER:
                // unit type gets a special editing graphic that only allows to select unit types with at least one
                // operation service
                final LeafTreeItem unitTypeLeaf = (LeafTreeItem) super.createChild(field, editable);
                unitTypeLeaf.setEditingGraphicFactory(EditingGraphicFactory.getInstance(ServiceStateUnitTypeEditingGraphic.class));
                return unitTypeLeaf;
        }
        // if no specialization needed call the super method
        return super.createChild(field, editable);
    }
}
