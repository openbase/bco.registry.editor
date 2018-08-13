package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.preset.LocationConfigTreeItem;
import org.openbase.bco.registry.editor.struct.preset.UnitConfigTreeItem;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import rst.domotic.unit.location.LocationConfigType.LocationConfig.LocationType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LocationChildIdEditingGraphic extends AbstractMessageEditingGraphic<UnitConfig> {

    public LocationChildIdEditingGraphic(ValueType<String> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(valueType, treeTableCell);
    }

    @Override
    protected List<UnitConfig> getMessages() throws CouldNotPerformException {
        final List<UnitConfig> unitConfigs = Registries.getUnitRegistry().getUnitConfigs(UnitType.LOCATION);
        final LocationConfigTreeItem locationTreeItem = (LocationConfigTreeItem) getValueType().getTreeItem().getParent().getParent();
        final UnitConfigTreeItem locationUnitTreeItem = (UnitConfigTreeItem) locationTreeItem.getParent();

        final LocationType type = locationTreeItem.getBuilder().getType();
        for (final UnitConfig unitConfig : new ArrayList<>(unitConfigs)) {
            if (unitConfig.getId().equals(getValueType().getValue())) {
                continue;
            }

            switch (type) {
                case ZONE:
                    if (unitConfig.getLocationConfig().getType() == LocationType.REGION) {
                        unitConfigs.remove(unitConfig);
                        continue;
                    }
                    break;
                case TILE:
                case REGION:
                    if (unitConfig.getLocationConfig().getType() != LocationType.REGION) {
                        unitConfigs.remove(unitConfig);
                        continue;
                    }
            }

            if (locationTreeItem.getBuilder().getChildIdList().contains(unitConfig.getId())) {
                unitConfigs.remove(unitConfig);
                continue;
            }

            if (unitConfig.getId().equals(locationUnitTreeItem.getBuilder().getId())) {
                unitConfigs.remove(unitConfig);
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
