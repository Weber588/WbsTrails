package wbs.trails.menus.build.options;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.trails.options.BooleanOption;
import wbs.trails.trails.options.DoubleOption;
import wbs.utils.util.menus.WbsMenu;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.string.WbsStrings;

import java.util.Collections;

public class BooleanOptionSlot<T> extends ConfigOptionSlot<T, Boolean> {
    private final BooleanOption<T> option;

    public BooleanOptionSlot(@NotNull WbsPlugin plugin, BooleanOption<T> option) {
        super(plugin, option, getItem(option, option.getDefaultValue()));

        this.option = option;

        setClickActionMenu(this::onClick);
    }

    private void onClick(WbsMenu menu, InventoryClickEvent event) {
        setCurrent(!getCurrent());
        menu.update(event.getSlot());
    }

    public void updateItem() {
        item.setType(materialFor(getCurrent()));

        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(nameFor(option.getName(), getCurrent()));

        item.setItemMeta(meta);
    }

    private static ItemStack getItem(BooleanOption<?> option, boolean enabled) {
        Material material = materialFor(enabled);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(material);
            assert meta != null;
        }

        meta.setDisplayName(nameFor(option.getName(), enabled));

        meta.setLore(Collections.singletonList(WbsTrails.getInstance().dynamicColourise("&7Click to toggle!")));

        item.setItemMeta(meta);

        return item;
    }

    private static String nameFor(String name, boolean enabled) {
        name = name.replace("_", " ");
        name = WbsStrings.capitalizeAll(name);
        name = WbsTrails.getInstance().dynamicColourise(name);

        if (enabled) {
            return "&a" + name + ": &lON";
        } else {
            return "&c" + name + ": &lOFF";
        }
    }

    private static Material materialFor(boolean enabled) {
        if (enabled) {
            return Material.LIME_STAINED_GLASS;
        } else {
            return Material.RED_STAINED_GLASS;
        }
    }
}
