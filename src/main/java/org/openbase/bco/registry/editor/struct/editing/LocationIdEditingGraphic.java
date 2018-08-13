package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.preset.UnitConfigTreeItem;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LocationIdEditingGraphic extends AbstractMessageEditingGraphic<UnitConfig> {

    public LocationIdEditingGraphic(ValueType<String> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(valueType, treeTableCell);
    }

    @Override
    protected List<UnitConfig> getMessages() throws CouldNotPerformException {
        final List<UnitConfig> unitConfigs = Registries.getUnitRegistry().getUnitConfigs(UnitType.LOCATION);

        // if the placement belongs to a location remove the location itself from the list
        final UnitConfigTreeItem locationUnitTreeItem = (UnitConfigTreeItem) getValueType().getTreeItem().getParent().getParent();
        if (locationUnitTreeItem.getBuilder().getUnitType() == UnitType.LOCATION) {
            for (UnitConfig unitConfig : new ArrayList<>(unitConfigs)) {
                if (unitConfig.getId().equals(locationUnitTreeItem.getBuilder().getId())) {
                    unitConfigs.remove(unitConfig);
                }
            }
        }

        return unitConfigs;
    }

    @Override
    protected String getCurrentValue(final UnitConfig message) {
        return message.getId();
    }

    @Override
    protected UnitConfig getMessage(final String value) throws CouldNotPerformException {
        return Registries.getUnitRegistry().getUnitConfigById(value);
    }

    @Override
    protected DescriptionGenerator<UnitConfig> getDescriptionGenerator() {
        return value -> {
            try {
                return ScopeGenerator.generateStringRep(value.getScope());
            } catch (CouldNotPerformException ex) {
                return value.getId();
            }
        };
    }
}
