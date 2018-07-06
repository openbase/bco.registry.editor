package org.openbase.bco.registry.editor.visual;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.Message;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.VBox;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.pattern.Remote.ConnectionState;
import org.openbase.jul.storage.registry.RegistryRemote;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RegistryTab<RD extends Message> extends Tab {

    final TabPane tabPane;
    final RegistryRemote<RD> registryRemote;
    final Map<FieldDescriptor, Tab> fieldDescriptorTabMap;
    private final Label statusInfoLabel;

    boolean isInitialized = false;

    public RegistryTab(final RegistryRemote<RD> registryRemote) {
        super(registryRemote.getName().replace("Registry", ""));
        this.registryRemote = registryRemote;

        this.tabPane = new TabPane();
        this.tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        this.fieldDescriptorTabMap = new HashMap<>();


        this.statusInfoLabel = new Label("Status Info Label");
        this.statusInfoLabel.setAlignment(Pos.CENTER);

        this.vBox = new VBox();
        this.vBox.setAlignment(Pos.CENTER);
        this.vBox.getChildren().addAll(statusInfoLabel, this);

        this.setContent(new ProgressIndicator());

        this.registryRemote.addDataObserver(new Observer<RD>() {
            @Override
            public void update(Observable<RD> source, RD data) throws Exception {
                updateTabs(data);
            }
        });
        this.registryRemote.addConnectionStateObserver(new Observer<ConnectionState>() {
            @Override
            public void update(Observable<ConnectionState> source, ConnectionState data) throws Exception {
                if(data != ConnectionState.CONNECTED) {
                    statusInfoLabel.setText();
                }
            }
        });
    }

    private void updateTabs(final RD data) {
        for (final FieldDescriptor field : data.getDescriptorForType().getFields()) {
            // skip fields that are not repeated an no messages
            if (field.getType() != Type.MESSAGE || !field.isRepeated()) {
                continue;
            }

            if (fieldDescriptorTabMap.containsKey(field)) {
                // TODO: update existing tab
            } else {
                // TODO: create new Tab
                final Tab tab = new Tab(field.getName());
                fieldDescriptorTabMap.put(field, tab);
                tabPane.getTabs().add(tab);
            }
        }
    }
}
