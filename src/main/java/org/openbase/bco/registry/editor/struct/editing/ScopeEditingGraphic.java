package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import rst.rsb.ScopeType.Scope;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ScopeEditingGraphic extends AbstractTextEditingGraphic<TextField, Scope.Builder> {

    public ScopeEditingGraphic(ValueType<Scope.Builder> valueType, TreeTableCell<Object, Object> treeTableCell) {
        super(new TextField(), valueType, treeTableCell);
    }

    @Override
    protected Scope.Builder getCurrentValue() {
        final Scope.Builder scope = getValueType().getValue();
        scope.clearComponent();
        for (final String component : getControl().getText().split("/")) {
            if (component.isEmpty()) {
                // ignore empty components
                continue;
            }
            scope.addComponent(ScopeGenerator.convertIntoValidScopeComponent(component));
        }
        return scope;
    }

    @Override
    protected void init(Scope.Builder value) {
        try {
            getControl().setText(ScopeGenerator.generateStringRep(value.build()));
        } catch (CouldNotPerformException ex) {
            logger.error("Could not init scope editing graphic", ex);
        }
    }
}
