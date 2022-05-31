package wbs.trails.menus.build.options;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.trails.trails.options.ConfigOption;
import wbs.trails.trails.options.OptionPair;
import wbs.utils.util.menus.PageSlot;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.function.Consumer;

public abstract class ConfigOptionSlot<T, V> extends PageSlot<ConfigOption<T, V>> {

    private V current;

    @Nullable
    private Consumer<V> onValueChange;

    public ConfigOptionSlot(@NotNull WbsPlugin plugin, ConfigOption<T, V> option, ItemStack item) {
        super(plugin, option, item);
        current = option.getDefaultValue();
    }

    protected V getCurrent() {
        return current;
    }

    public void setCurrent(V current) {
        this.current = current;
        if (onValueChange != null) {
            onValueChange.accept(current);
        }

        updateItem();
    }

    public OptionPair<T, V> getPair() {
        return new OptionPair<>(pageItem, current);
    }

    public void setOnValueChange(@Nullable Consumer<V> onValueChange) {
        this.onValueChange = onValueChange;
    }

    public V fromT(T trail) {
        setCurrent(pageItem.fromT(trail).getValue());
        return current;
    }

    public abstract void updateItem();
}
