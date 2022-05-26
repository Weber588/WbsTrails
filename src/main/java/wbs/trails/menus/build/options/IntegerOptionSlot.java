package wbs.trails.menus.build.options;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.trails.options.IntegerOption;
import wbs.utils.util.WbsMath;
import wbs.utils.util.menus.WbsMenu;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.string.WbsStrings;

import java.util.Arrays;

public class IntegerOptionSlot<T> extends ConfigOptionSlot<T, Integer> {
    private final IntegerOption<T> option;

    public IntegerOptionSlot(@NotNull WbsPlugin plugin, IntegerOption<T> option) {
        super(plugin, option, getItem(option, option.getDefaultValue()));

        this.option = option;
        setClickActionMenu(this::onClick);
    }

    private void onClick(WbsMenu menu, InventoryClickEvent event) {
        switch (event.getClick()) {
            case LEFT:
            case SHIFT_LEFT:
                current++;
                break;
            case RIGHT:
            case SHIFT_RIGHT:
                current--;
                break;
            case MIDDLE: // = middle of min and max
                current = (option.getMax() - option.getMin()) / 2 + option.getMin();
                break;
            case NUMBER_KEY: // Set to number if in range
                current = event.getHotbarButton() + 1;
                break;
            case DROP:
            case CONTROL_DROP: // random
                current = (int) (Math.random() * (option.getMax() - option.getMin()) + option.getMin());
                break;
        }

        current = WbsMath.clamp(option.getMin(), option.getMax(), current);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        updateLore(meta, current);

        item.setItemMeta(meta);
        menu.update(event.getSlot());
    }

    private static ItemStack getItem(IntegerOption<?> option, int value) {
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

    private static void updateLore(ItemMeta meta, int value) {
        meta.setLore(WbsTrails.getInstance().colouriseAll(
                Arrays.asList("&6Current: &b" + value,
                        "&7Left click: &b+1",
                        "&7Right click: &b-1")
        ));
    }
}
