package wbs.trails.trails;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wbs.trails.WbsTrails;
import wbs.trails.menus.MainMenu;
import wbs.trails.menus.build.BuildMenu;
import wbs.utils.util.menus.MenuSlot;

import java.util.Objects;

public final class TrailMenuUtils {
    private TrailMenuUtils() {}

    private static final WbsTrails plugin = WbsTrails.getInstance();

    // ================= //
    //       Misc        //
    // ================= //

    private static MenuSlot outlineSlot;
    public static MenuSlot getOutlineSlot() {
        if (outlineSlot != null) return outlineSlot;

        Material outlineMaterial = Material.BLUE_STAINED_GLASS_PANE;

        ItemStack outlineItem = new ItemStack(outlineMaterial);
        ItemMeta meta = Objects.requireNonNull(
                Bukkit.getItemFactory().getItemMeta(outlineMaterial)
        );
        meta.setDisplayName("&r");

        outlineSlot = new MenuSlot(plugin, outlineItem);
        return outlineSlot;
    }

    // ================= //
    //       Menus       //
    // ================= //

    private static MainMenu mainMenu;
    public static MainMenu getMainMenu() {
        if (mainMenu != null) return mainMenu;

        mainMenu = new MainMenu(plugin);

        return mainMenu;
    }

    // ================= //
    //     Menu Links    //
    // ================= //

    public static MenuSlot getMainMenuLink() {
        Material material = Material.CLOCK;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = Objects.requireNonNull(
                Bukkit.getItemFactory().getItemMeta(material)
        );
        meta.setDisplayName("&9Main Menu");

        item.setItemMeta(meta);

        MenuSlot mainMenuLink = new MenuSlot(plugin, item);

        mainMenuLink.setClickAction(
                (event) -> {
                    getMainMenu().showTo((Player) event.getWhoClicked());
                }
        );

        return mainMenuLink;
    }

    public static MenuSlot getBuildLink() {
        Material material = Material.CRAFTING_TABLE;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = Objects.requireNonNull(
                Bukkit.getItemFactory().getItemMeta(material)
        );
        meta.setDisplayName("&3Build Trail");

        item.setItemMeta(meta);

        MenuSlot buildLink = new MenuSlot(plugin, item);

        buildLink.setClickAction(
                (event) -> {
                    Player player = (Player) event.getWhoClicked();
                    new BuildMenu(plugin, player).showTo(player);
                }
        );

        return buildLink;
    }


}
