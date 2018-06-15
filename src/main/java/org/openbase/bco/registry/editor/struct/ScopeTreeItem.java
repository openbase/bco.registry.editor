package org.openbase.bco.registry.editor.struct;

import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.openbase.bco.registry.editor.struct.value.DescriptionGenerator;
import org.openbase.bco.registry.editor.struct.value.EditingGraphicFactory;
import org.openbase.bco.registry.editor.struct.value.ScopeEditingGraphicFactory;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import rst.rsb.ScopeType.Scope;
import rst.rsb.ScopeType.Scope.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ScopeTreeItem extends BuilderTreeItem<Scope.Builder> {

    public ScopeTreeItem(FieldDescriptor fieldDescriptor, Builder builder) {
        super(fieldDescriptor, builder);
    }

    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() {
        // empty list because scope will be represented as a string
        return FXCollections.observableArrayList();
    }

    @Override
    protected DescriptionGenerator<Builder> getDescriptionGenerator() {
        //TODO: maybe descriptor can just be a helper class so that not that many will be generated?
        return new DescriptionGenerator<Builder>() {
            @Override
            public String getValueDescription(Builder value) {
                try {
                    return ScopeGenerator.generateStringRep(value.build());
                } catch (CouldNotPerformException ex) {
                    logger.error("Could not generate string rep for scope", ex);
                    return "";
                }
            }

            @Override
            public String getDescription(Builder value) {
                return fieldDescriptor.getName();
            }
        };
    }

    @Override
    protected EditingGraphicFactory<Builder> getEditingGraphicFactory() {
        return ScopeEditingGraphicFactory.getInstance();
    }
}
