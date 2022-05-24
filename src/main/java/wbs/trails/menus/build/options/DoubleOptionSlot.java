package wbs.trails.menus.build.options;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.trails.options.DoubleOption;
import wbs.utils.util.WbsMath;
import wbs.utils.util.menus.WbsMenu;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.string.WbsStrings;

import java.util.Arrays;

public class DoubleOptionSlot<T> extends ConfigOptionSlot<T, Double, DoubleOption<T>> {
    public DoubleOptionSlot(@NotNull WbsPlugin plugin, DoubleOption<T> option) {
        super(plugin, option, getItem(option, option.getDefaultValue()));

        setClickActionMenu(this::onClick);
    }

    private void onClick(WbsMenu menu, InventoryClickEvent event) {
        switch (event.getClick()) {
            case LEFT:
                current++;
                break;
            case SHIFT_LEFT:
                current += 0.1;
                break;
            case RIGHT:
                current--;
                break;
            case SHIFT_RIGHT:
                current -= 0.1;
                break;
            case MIDDLE: // = middle of min and max
                current = (option.getMax() - option.getMin()) / 2 + option.getMin();
                break;
            case NUMBER_KEY: // Set to number if in range, or number / 10 if shifting
                current = (double) event.getHotbarButton() + 1;
                if (event.isShiftClick()) {
                    current /= 10;
                }
                break;
            case DROP:
            case CONTROL_DROP: // random
                current = Math.random() * (option.getMax() - option.getMin()) + option.getMin();
                break;
        }

        current = WbsMath.clamp(option.getMin(), option.getMax(), current);
        current = WbsMath.roundTo(current, 2);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        updateLore(meta, current);

        item.setItemMeta(meta);
        menu.update(event.getSlot());
    }

    private static ItemStack getItem(DoubleOption<?> option, double value) {
        Material material = Material.OAK_SIGN;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(material);
            assert meta != null;
        }

        String name = option.getName().replace("_", " ");
        name = WbsStrings.capitalizeAll(name);
        name = WbsTrails.getInstance().dynamicColourise("&3" + name);

        meta.setDisplayName(name);
        updateLore(meta, value);

        item.setItemMeta(meta);

        return item;
    }

    private static void updateLore(ItemMeta meta, double value) {
        meta.setLore(WbsTrails.getInstance().colouriseAll(
                Arrays.asList("&6Current: &b" + value,
                        "&7Left Click: &b+1",
                        "&7Right Click: &b-1",
                        "&7Shift Left Click: &b+0.1",
                        "&7Shift Right Click: &b-0.1")
        ));
    }
}
