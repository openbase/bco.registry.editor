package org.openbase.bco.registry.editor.visual;

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

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.Message;
import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import org.openbase.bco.registry.editor.util.FieldPathDescriptionProvider;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.type.domotic.state.ConnectionStateType.ConnectionState;
import org.openbase.jul.processing.StringProcessor;
import org.openbase.jul.storage.registry.RegistryRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RegistryRemoteTab<RD extends Message> extends TabWithStatusLabel {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryRemoteTab.class);

    private final TabPane tabPane;
    private final RegistryRemote<RD> registryRemote;
    private final Map<FieldDescriptor, RegistryTab<RD>> fieldDescriptorTabMap;

    /**
     * This value is used to save if the tab has been selected at least once.
     * This is useful for a fast startup because if the tab is never selected its content does not need to be initialized.
     */
    private boolean onceSelected = false;

    private List<Integer> sortOrder;
    private Map<Integer, FieldPathDescriptionProvider[]> groupingMap;

    public RegistryRemoteTab(final RegistryRemote<RD> registryRemote) {
        this(registryRemote, null);
    }

    public RegistryRemoteTab(final RegistryRemote<RD> registryRemote, final Map<Integer, FieldPathDescriptionProvider[]> groupingMap) {
        this(registryRemote, groupingMap, null);
    }

    public RegistryRemoteTab(final RegistryRemote<RD> registryRemote, final Map<Integer, FieldPathDescriptionProvider[]> groupingMap, final List<Integer> sortOrder) {
        super(registryRemote.getClass().getSimpleName().replace("Remote", ""));
        this.registryRemote = registryRemote;
        this.sortOrder = sortOrder;
        this.groupingMap = groupingMap;

        this.tabPane = new TabPane();
        this.tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        this.fieldDescriptorTabMap = new HashMap<>();

        this.setInternalContent(new ProgressIndicator());

        this.registryRemote.addDataObserver((source, data) -> {
            // only update internal registry tabs if this tab was at one time selected
            if (onceSelected) {
                updateTabs(data);
            }
        });
        this.registryRemote.addConnectionStateObserver((source, data) -> {
            if (data != ConnectionState.State.CONNECTED) {
                Platform.runLater(() -> setStatusText(StringProcessor.transformUpperCaseToPascalCase(data.name()) + "!"));
                // TODO: make tabs uneditable or at least changes cannot be applied, also update status label style
            } else {
                Platform.runLater(this::clearStatusLabel);
            }
        });

        setOnSelectionChanged(event -> {
            if (!onceSelected) {
                // save that first time selected and if available update internal tabs
                if (registryRemote.isDataAvailable()) {
                    try {
                        updateTabs(registryRemote.getData());
                    } catch (NotAvailableException ex) {
                        // this should not happen since is data available is checked
                        ExceptionPrinter.printHistory("Could not update registry tabs", ex, LOGGER);
                    }
                }
                onceSelected = true;
            }
        });
    }

    private void updateTabs(final RD data) {
        Platform.runLater(() -> {
            setInternalContent(tabPane);

            if (fieldDescriptorTabMap.isEmpty()) {
                final List<FieldDescriptor> fieldDescriptorList = new ArrayList<>();
                for (final FieldDescriptor field : data.getDescriptorForType().getFields()) {
                    // skip fields that are not repeated and no messages
                    if (field.getType() != Type.MESSAGE || !field.isRepeated()) {
                        continue;
                    }
                    fieldDescriptorList.add(field);
                }

                if (sortOrder != null) {
                    fieldDescriptorList.sort(buildComparator(sortOrder));
                }

                for (FieldDescriptor field : fieldDescriptorList) {
                    final RegistryTab<RD> registryTab;
                    if (groupingMap != null && groupingMap.containsKey(field.getNumber())) {
                        registryTab = new RegistryTab<>(field, data, groupingMap.get(field.getNumber()));
                    } else {
                        registryTab = new RegistryTab<>(field, data);
                    }
                    registryTab.getContent().prefHeight(tabPane.getHeight());
                    tabPane.getTabs().add(registryTab);
                    // this is done in its own method because adding the first tab will select it but the tab pane
                    // is then not yet available from the tab, therefore width properties have to be added afterwards
                    registryTab.manageWidth();
                    fieldDescriptorTabMap.put(field, registryTab);
                }
                return;
            }

            for (final FieldDescriptor fieldDescriptor : fieldDescriptorTabMap.keySet()) {
                try {
                    fieldDescriptorTabMap.get(fieldDescriptor).update(data);
                } catch (CouldNotPerformException ex) {
                    ExceptionPrinter.printHistory("Could not update tab[" + StringProcessor.
                            transformToPascalCase(fieldDescriptor.getName()) + "]", ex, LOGGER);
                }
            }
        });
    }

    public RegistryRemote<RD> getRegistryRemote() {
        return registryRemote;
    }

    @Override
    public String toString() {
        return registryRemote.getClass().getSimpleName() + TabPane.class.getSimpleName();
    }

    private Comparator<FieldDescriptor> buildComparator(final List<Integer> sortOrder) {
        final Map<Integer, Integer> priorityMap = new HashMap<>();
        int i = sortOrder.size();
        for (Integer integer : sortOrder) {
            priorityMap.put(integer, i);
            i = i - 1;
        }

        return (o1, o2) -> {
            if (priorityMap.containsKey(o1.getNumber())) {
                if (priorityMap.containsKey(o2.getNumber())) {
                    return (int) Math.signum(priorityMap.get(o2.getNumber()) - priorityMap.get(o1.getNumber()));
                } else {
                    return -1;
                }
            } else {
                if (priorityMap.containsKey(o2.getNumber())) {
                    return +1;
                } else {
                    return 0;
                }
            }
        };
    }
}
