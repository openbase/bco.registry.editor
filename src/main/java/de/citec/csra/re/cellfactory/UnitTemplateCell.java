/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.csra.re.RegistryEditor;
import de.citec.csra.re.struct.node.Node;
import de.citec.csra.re.struct.node.UnitTemplateContainer;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jul.exception.CouldNotPerformException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import rst.homeautomation.unit.UnitTemplateType;

/**
 *
 * @author thuxohl
 */
public class UnitTemplateCell extends ValueCell {

    public UnitTemplateCell(DeviceRegistryRemote deviceRegistryRemote) {
        super(deviceRegistryRemote, null, null, null, null);

        applyButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Thread thread = new Thread(
                        new Task<Boolean>() {
                            @Override
                            protected Boolean call() throws Exception {
                                RegistryEditor.setModified(false);
                                UnitTemplateContainer container = (UnitTemplateContainer) getItem();

                                UnitTemplateType.UnitTemplate unitTemplate = container.getBuilder().build();
                                try {
                                    if (deviceRegistryRemote.containsUnitTemplate(unitTemplate)) {
                                        deviceRegistryRemote.updateUnitTemplate(unitTemplate);
                                    }
                                    container.setChanged(false);
                                } catch (CouldNotPerformException ex) {
                                    logger.warn("Could not register or update unit template [" + unitTemplate + "]", ex);
                                }
                                return true;
                            }
                        });
                thread.setDaemon(true);
                thread.start();
            }
        });

        cancel.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Thread thread = new Thread(
                        new Task<Boolean>() {
                            @Override
                            protected Boolean call() throws Exception {
                                RegistryEditor.setModified(false);
                                UnitTemplateContainer container = (UnitTemplateContainer) getItem();

                                UnitTemplateType.UnitTemplate unitTemplate = container.getBuilder().build();
                                try {
                                    if (container.getNewNode()) {
                                        container.getParent().getChildren().remove(container);
                                    } else {
                                        int index = container.getParent().getChildren().indexOf(container);
                                        container.getParent().getChildren().set(index, new UnitTemplateContainer(deviceRegistryRemote.getUnitTemplateById(unitTemplate.getId()).toBuilder()));
                                    }
                                } catch (CouldNotPerformException ex) {
                                    logger.warn("Could cancel update of [" + unitTemplate + "]", ex);
                                }
                                return true;
                            }
                        });
                thread.setDaemon(true);
                thread.start();
            }
        });
    }

    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (item instanceof UnitTemplateContainer) {
            UnitTemplateContainer container = (UnitTemplateContainer) item;
            if ((container.getNewNode() || container.hasChanged()) && getGraphic() != buttonBox) {
                setGraphic(buttonBox);
            } else if (!(container.getNewNode() || container.hasChanged()) && getGraphic() != null) {
                setGraphic(null);
            }
            container.getChanged().addListener(new ChangeListener<Boolean>() {

                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            if (newValue && getGraphic() != buttonBox) {
                                setGraphic(buttonBox);
                            } else if (getGraphic() != null) {
                                setGraphic(null);
                            }
                        }
                    });
                }
            });
        } else {
            if (item != null && ((!"scope".equals(item.getDescriptor()) && (!"id".equals(item.getDescriptor()))))) {
                setGraphic(null);
            }
        }
    }
}
