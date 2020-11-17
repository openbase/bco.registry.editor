package org.openbase.bco.registry.editor.struct.editing;

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

import com.google.protobuf.Message;
import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.Pane;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.service.*;
import org.openbase.bco.registry.editor.struct.preset.ServiceStateDescriptionTreeItem;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.protobuf.processing.ProtoBufJSonProcessor;

/**
 * Special editing graphic for the service attribute in a service state description.
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ServiceStateAttributeEditingGraphic extends AbstractEditingGraphic<Pane, String> {

    private ServiceStateEditingGraphic serviceStateEditingGraphic;

    public ServiceStateAttributeEditingGraphic(final ValueType<String> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new Pane(), valueType, treeTableCell);
    }

    @Override
    protected String getCurrentValue() {
        if (serviceStateEditingGraphic == null) {
            return "";
        }

        try {
            return new ProtoBufJSonProcessor().serialize(serviceStateEditingGraphic.getServiceState());
        } catch (CouldNotPerformException e) {
            ExceptionPrinter.printHistory(e, logger);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void init(final String value) {
        // retrieve service state description
        final ServiceStateDescriptionTreeItem parent = (ServiceStateDescriptionTreeItem) getValueType().getTreeItem().getParent();
        // deserialize current value
        Message deserialize = null;
        try {
            // skipp deserialization if value is empty
            if (!value.isEmpty()) {
                deserialize = new ProtoBufJSonProcessor().deserialize(value, parent.getBuilder().getServiceStateClassName());
            }
        } catch (CouldNotPerformException ex) {
            // log error if it one occurs
            ExceptionPrinter.printHistory(ex, logger);
        }

        // based on service type decide on the editing graphic
        switch (parent.getBuilder().getServiceType()) {
            case COLOR_STATE_SERVICE:
                serviceStateEditingGraphic = new ColorStateEditingGraphic();
                break;
            case POWER_STATE_SERVICE:
                serviceStateEditingGraphic = new PowerStateEditingGraphic();
                break;
            case ACTIVATION_STATE_SERVICE:
            case DISCOVERY_STATE_SERVICE:
                serviceStateEditingGraphic = new ActivationStateEditingGraphic();
                break;
            case BRIGHTNESS_STATE_SERVICE:
                serviceStateEditingGraphic = new BrightnessStateEditingGraphic();
                break;
            case STANDBY_STATE_SERVICE:
                serviceStateEditingGraphic = new StandbyStateEditingGraphic();
                break;
            default:
                // just cancel the edit because service type not yet supported TODO: inform the user about this
                getTreeTableCell().cancelEdit();
                return;
        }
        // init the editing graphic with the de-serialized value
        serviceStateEditingGraphic.init(deserialize);
        // if the editing graphic wants to add a custom handler that commits changes this is done in this method
        serviceStateEditingGraphic.addCommitEditEventHandler(this);
        // add the editing graphic to the hBox displayed
        getControl().getChildren().add(serviceStateEditingGraphic.getGraphic());
    }
}
