package wbs.trails.menus.build.options;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import wbs.trails.trails.options.ConfigOption;
import wbs.trails.trails.options.OptionPair;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.plugin.WbsPlugin;

public abstract class ConfigOptionSlot<T, V> extends MenuSlot {

    protected final ConfigOption<T, V> option;
    protected V current;

    public ConfigOptionSlot(@NotNull WbsPlugin plugin, ConfigOption<T, V> option, ItemStack item) {
        super(plugin, item);
        this.option = option;
        current = option.getDefaultValue();
    }

    public OptionPair<T, V> getPair() {
        return new OptionPair<>(option, current);
    }
}
