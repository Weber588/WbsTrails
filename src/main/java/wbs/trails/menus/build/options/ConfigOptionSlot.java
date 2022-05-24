package wbs.trails.menus.build.options;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.trails.options.*;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.plugin.WbsPlugin;

public abstract class ConfigOptionSlot<T, V, C extends ConfigOption<T, V>> extends MenuSlot {
    @SuppressWarnings("unchecked")
    public static <T, V, C extends ConfigOption<T, V>> ConfigOptionSlot<T, V, C> toSlot(ConfigOption<T, V> option) {
        if (option instanceof DoubleOption) {
            return (ConfigOptionSlot<T, V, C>) new DoubleOptionSlot<>(WbsTrails.getInstance(), (DoubleOption<T>) option);
        } else if (option instanceof IntegerOption) {
            return (ConfigOptionSlot<T, V, C>) new IntegerOptionSlot<>(WbsTrails.getInstance(), (IntegerOption<T>) option);
        } else if (option instanceof BooleanOption) {
            return (ConfigOptionSlot<T, V, C>) new BooleanOptionSlot<>(WbsTrails.getInstance(), (BooleanOption<T>) option);
        } else {
            throw new NotImplementedException("Missing config option type: " + option.getClass().getCanonicalName());
        }
    }

    protected final C option;
    protected V current;

    public ConfigOptionSlot(@NotNull WbsPlugin plugin, C option, ItemStack item) {
        super(plugin, item);
        this.option = option;
        current = option.getDefaultValue();
    }

    public OptionPair<T, V> getPair() {
        return new OptionPair<>(option, current);
    }
}
