package org.openbase.bco.registry.editor.struct.preset;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2020 openbase.org
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
import org.openbase.jul.extension.type.processing.ScopeProcessor;
import org.openbase.jul.visual.javafx.transform.JFXColorToHSBColorTransformer;
import org.openbase.type.domotic.service.ServiceDescriptionType.ServiceDescription;
import org.openbase.type.domotic.service.ServiceStateDescriptionType.ServiceStateDescription;
import org.openbase.type.domotic.service.ServiceStateDescriptionType.ServiceStateDescription.Builder;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServicePattern;
import org.openbase.type.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import org.openbase.type.domotic.state.ActivationStateType.ActivationState;
import org.openbase.type.domotic.state.BrightnessStateType.BrightnessState;
import org.openbase.type.domotic.state.ColorStateType.ColorState;
import org.openbase.type.domotic.state.PowerStateType.PowerState;
import org.openbase.type.domotic.state.StandbyStateType.StandbyState;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import org.openbase.type.vision.HSBColorType.HSBColor;

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
                    case ServiceStateDescription.SERVICE_STATE_FIELD_NUMBER:
                        handleServiceStateUpdate();
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
        final FieldDescriptor attributeField = ServiceStateDescription.getDescriptor().findFieldByNumber(ServiceStateDescription.SERVICE_STATE_FIELD_NUMBER);
        final FieldDescriptor attributeTypeField = ServiceStateDescription.getDescriptor().findFieldByNumber(ServiceStateDescription.SERVICE_STATE_CLASS_NAME_FIELD_NUMBER);

        // retrieve tree items belonging to those descriptors
        final LeafTreeItem<String> attributeLeaf = (LeafTreeItem<String>) getDescriptorChildMap().get(attributeField);
        final LeafTreeItem<String> attributeTypeLeaf = (LeafTreeItem<String>) getDescriptorChildMap().get(attributeTypeField);

        if (getBuilder().getServiceType() != ServiceType.UNKNOWN) {
            // if service type is set and not unknown the attribute becomes editable and the attribute type is updated
            // note: the order here is important, if the attribute type is not updated before the attribute parsing the empty json value will fail
            attributeTypeLeaf.update(Registries.getTemplateRegistry().getServiceStateClassName(getBuilder().getServiceType()));
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
        // if the unit type is unknown, no changes need to be applied because all old values are still valid
        if (getBuilder().getUnitType() == UnitType.UNKNOWN) {
            return;
        }

        // retrieve descriptors
        final FieldDescriptor serviceTypeField = ServiceStateDescription.getDescriptor().findFieldByNumber(ServiceStateDescription.SERVICE_TYPE_FIELD_NUMBER);
        final FieldDescriptor unitIdField = ServiceStateDescription.getDescriptor().findFieldByNumber(ServiceStateDescription.UNIT_ID_FIELD_NUMBER);

        // retrieve tree items belonging to those descriptors
        LeafTreeItem<String> unitIdLeaf = (LeafTreeItem<String>) getDescriptorChildMap().get(unitIdField);
        LeafTreeItem<EnumValueDescriptor> serviceTypeLeaf = (LeafTreeItem<EnumValueDescriptor>) getDescriptorChildMap().get(serviceTypeField);

        // if the unit id was set verify that the unit still belongs to the new unit type or is a sub unit type
        final UnitType unitType = Registries.getUnitRegistry().getUnitConfigById(getBuilder().getUnitId()).getUnitType();
        final List<UnitType> subUnitTypes = Registries.getTemplateRegistry().getSubUnitTypes(getBuilder().getUnitType());
        if (getBuilder().getUnitType() != unitType && !subUnitTypes.contains(unitType)) {
            unitIdLeaf.update("");
        }

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
    private void handleServiceStateUpdate() {
        // do nothing if the service attribute has been cleared
        if (getBuilder().getServiceState().isEmpty()) {
            return;
        }

        // retrieve service attribute leaf
        final FieldDescriptor attributeField = ServiceStateDescription.getDescriptor().findFieldByNumber(ServiceStateDescription.SERVICE_STATE_FIELD_NUMBER);
        final LeafTreeItem<String> attributeLeaf = (LeafTreeItem<String>) getDescriptorChildMap().get(attributeField);
        // create and set mew value graphic
        attributeLeaf.setValueGraphic(getServiceStateValueGraphic());
    }

    /**
     * Create a nicer value graphic representation for the service attribute. This is done by de-serializing it
     * and creating a description graphic from the service state.
     * If the value cannot be de-serialized or no nicer representation for the service state is available a default
     * label with the service state string is returned.
     *
     * @return a value graphic for the service attribute
     */
    private Node getServiceStateValueGraphic() {
        if (!getBuilder().hasServiceState() || getBuilder().getServiceState().isEmpty()) {
            return new Label();
        }
        try {
            // deserialize service attribute
            final Message serviceState = new ProtoBufJSonProcessor().deserialize(getBuilder().getServiceState(), getBuilder().getServiceStateClassName());
            switch (getBuilder().getServiceType()) {
                case COLOR_STATE_SERVICE:
                    // build a java fx color from the color state
                    final HSBColor hsbColor = ((ColorState) serviceState).getColor().getHsbColor();
                    final Color hsb = JFXColorToHSBColorTransformer.transform(hsbColor);
                    // create a rectangle of that color and set it as the value graphic
                    final Rectangle rectangle = new Rectangle(15, 15);
                    rectangle.setFill(hsb);
                    return rectangle;
                case ACTIVATION_STATE_SERVICE:
                    // for an activation state display the enum value
                    final ActivationState.State activation = ((ActivationState) serviceState).getValue();
                    return new Label(activation.name());
                case POWER_STATE_SERVICE:
                    // for a power state display the enum value
                    final PowerState.State power = ((PowerState) serviceState).getValue();
                    return new Label(power.name());
                case STANDBY_STATE_SERVICE:
                    // for a standby state display the enum value
                    final StandbyState.State standby = ((StandbyState) serviceState).getValue();
                    return new Label(standby.name());
                case BRIGHTNESS_STATE_SERVICE:
                    // for a brightness state format and display the brightness value
                    final DecimalFormat decimalFormat = new DecimalFormat("##.##");
                    final double brightness = ((BrightnessState) serviceState).getBrightness();
                    return new Label(decimalFormat.format(brightness));
                default:
                    // show to the user that this service is currently not supported, meaning it cannot be edited
                    return new Label("Uneditable Attribute: " + getBuilder().getServiceState());
            }
        } catch (CouldNotPerformException ex) {
            // log the exception and return a default value graphic
            ExceptionPrinter.printHistory("Could not generate value graphic for service attribute with type[" + getBuilder().getServiceType().name() + "]", ex, logger);
            return new Label(getBuilder().getServiceState());
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
                // unit id is always editable, gets a description generator displaying the scope
                // and gets a description graphic filtering units matching the setting of this service state description
                final LeafTreeItem<String> unitIdLeaf = new LeafTreeItem<>(field, getBuilder().getUnitId(), editable);
                unitIdLeaf.setEditingGraphicFactory(EditingGraphicFactory.getInstance(ServiceStateUnitIdEditingGraphic.class));
                unitIdLeaf.setDescriptionGenerator(unitId -> {
                    try {
                        return ScopeProcessor.generateStringRep(Registries.getUnitRegistry().getUnitConfigById(unitId).getScope());
                    } catch (CouldNotPerformException e) {
                        return unitId;
                    }
                });
                return unitIdLeaf;
            case ServiceStateDescription.SERVICE_STATE_CLASS_NAME_FIELD_NUMBER:
                // service attribute type is derived from the service type and thus uneditable
                return super.createChild(field, false);
            case ServiceStateDescription.SERVICE_STATE_FIELD_NUMBER:
                // service attribute is only editable if the service type is set, its value graphic is created and
                // it received a special editing graphic
                LeafTreeItem<String> serviceStateLeaf = new LeafTreeItem<>(field, getBuilder().getServiceState(), getBuilder().getServiceType() != ServiceType.UNKNOWN);
                serviceStateLeaf.setEditingGraphicFactory(EditingGraphicFactory.getInstance(ServiceStateAttributeEditingGraphic.class));
                serviceStateLeaf.setValueGraphic(getServiceStateValueGraphic());
                return serviceStateLeaf;
            case ServiceStateDescription.SERVICE_TYPE_FIELD_NUMBER:
                // service type is always editable and gets a special editing graphic only
                // displaying operation services according to the unit type
                final LeafTreeItem serviceTypeLeaf = (LeafTreeItem) super.createChild(field, editable);
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
