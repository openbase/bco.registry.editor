package org.openbase.bco.registry.editor;

import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.openbase.bco.registry.editor.struct.ListTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.visual.cell.SecondCell;
import org.openbase.bco.registry.editor.visual.cell.TestCell;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jps.core.JPService;
import org.openbase.jps.preset.JPVerbose;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import rst.domotic.registry.UnitRegistryDataType.UnitRegistryData;

import java.util.ArrayList;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class TestEditor extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        JPService.registerProperty(JPVerbose.class, true);
        String[] args = {"-v"};
        JPService.parseAndExitOnError(args);
        final TreeTableColumn<ValueType, ValueType> descriptionColumn = new TreeTableColumn<>();
        final TreeTableColumn<ValueType, ValueType> valueColumn = new TreeTableColumn<>();

        descriptionColumn.setPrefWidth(400);
        valueColumn.setPrefWidth(368);
        descriptionColumn.setCellValueFactory(param -> param.getValue().valueProperty());
        valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());
        descriptionColumn.setCellFactory(param -> new SecondCell());
        valueColumn.setCellFactory(param -> new TestCell());
        final TreeTableView<ValueType> treeTableView = new TreeTableView<>();
        treeTableView.getColumns().addAll(descriptionColumn, valueColumn);
        treeTableView.showRootProperty().setValue(false);


        FieldDescriptor fieldDescriptor = ProtoBufFieldProcessor.getFieldDescriptor(UnitRegistryData.getDefaultInstance(), UnitRegistryData.DAL_UNIT_CONFIG_FIELD_NUMBER);
        UnitRegistryData data = Registries.getUnitRegistry(true).getData();
        ListTreeItem<UnitRegistryData.Builder> root = new ListTreeItem<>(fieldDescriptor, Registries.getUnitRegistry(true).getData().toBuilder(), true);
        ArrayList<TreeItem> list = new ArrayList<>();
        root.setExpanded(true);
        treeTableView.setRoot(root);
        list.add(treeTableView.getRoot());
        System.out.println(list.get(0));

        final Scene scene = new Scene(treeTableView, 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        System.exit(0);
    }
}
