package wbs.trails.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wbs.trails.TrailsController;
import wbs.trails.WbsTrails;
import wbs.trails.menus.build.ChooseTrailMenu;
import wbs.trails.trails.Trail;
import wbs.trails.trails.data.DataProducer;
import wbs.trails.trails.options.ConfigOption;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.PageSlot;
import wbs.utils.util.string.WbsStrings;

import java.util.LinkedList;
import java.util.List;
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

        outlineItem.setItemMeta(meta);

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

        mainMenuLink.setClickAction((event) ->
                plugin.runSync(() ->
                        getMainMenu().showTo((Player) event.getWhoClicked()))
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

                    plugin.runSync(() ->
                            new ChooseTrailMenu(plugin, player).showTo(player));
                }
        );

        return buildLink;
    }

    public static MenuSlot getPresetsLink() {
        Material material = Material.KNOWLEDGE_BOOK;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = Objects.requireNonNull(
                Bukkit.getItemFactory().getItemMeta(material)
        );
        meta.setDisplayName("&dPreset Trails");

        item.setItemMeta(meta);

        MenuSlot buildLink = new MenuSlot(plugin, item);

        buildLink.setClickAction(
                (event) -> {
                    Player player = (Player) event.getWhoClicked();

                    plugin.runSync(() ->
                            new PresetsMenu(plugin, player).showTo(player));
                }
        );

        return buildLink;
    }

    public static MenuSlot getCurrentTrailsLink() {
        Material material = Material.WRITABLE_BOOK;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = Objects.requireNonNull(
                Bukkit.getItemFactory().getItemMeta(material)
        );
        meta.setDisplayName("&bYour Trails");

        item.setItemMeta(meta);

        MenuSlot buildLink = new MenuSlot(plugin, item);

        buildLink.setClickAction(
                (event) -> {
                    Player player = (Player) event.getWhoClicked();

                    if (TrailsController.getInstance().getTrails(player).isEmpty()) {
                        plugin.sendMessage("&cYou don't have any trails active right now!", player);
                    } else {
                        plugin.runSync(() ->
                                new CurrentTrailsMenu(plugin, player).showTo(player));
                    }
                }
        );

        return buildLink;
    }

    public static <T extends Trail<T>> PageSlot<Trail<?>> getTrailPreview(Trail<T> trail, boolean showOptions) {
        List<String> lore = new LinkedList<>();

        if (trail.getParticle() != null) {
            lore.add("&6Particle&7: &b" + WbsEnums.toPrettyString(trail.getParticle()));
        }

        if (trail.getData() != null) {
            DataProducer<?, ?> producer = trail.getData();

            for (String line : producer.getValueDisplays()) {
                lore.add("  " + line);
            }
        }

        if (showOptions) {
            for (ConfigOption<T, ?> option : trail.getRegistration().getOptions()) {
                //noinspection unchecked
                lore.add("&6" + WbsStrings.capitalize(option.getName()) + "&7: &b" + option.fromT((T) trail).getValue());
            }
        }

        return new PageSlot<>(plugin,
                trail,
                trail.getRegistration().getMaterial(),
                "&b" + WbsStrings.capitalizeAll(trail.getRegistration().getName()) + " Trail",
                true,
                lore);
    }
}
