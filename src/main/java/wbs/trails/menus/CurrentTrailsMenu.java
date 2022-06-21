package wbs.trails.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import wbs.trails.TrailsController;
import wbs.trails.WbsTrails;
import wbs.trails.listeners.ChatStringReader;
import wbs.trails.menus.build.ChooseParticleMenu;
import wbs.trails.menus.build.MenuPage;
import wbs.trails.trails.Trail;
import wbs.trails.trails.presets.PresetManager;
import wbs.trails.trails.presets.PresetTrail;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.PageSlot;
import wbs.utils.util.menus.PagedMenu;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.LinkedList;
import java.util.List;

public class CurrentTrailsMenu extends PagedMenu<Trail<?>> implements MenuPage {

    private final Player player;

    public CurrentTrailsMenu(WbsPlugin plugin, Player player) {
        this(plugin, player, 0);
    }

    public CurrentTrailsMenu(WbsPlugin plugin, Player player, int page) {
        super(plugin,
                TrailsController.getInstance().getActiveTrails(player),
                "&5Your Trails",
                "current_trails:" + player.getUniqueId(),
                1, // rowStart
                4, // maxRows
                1, // minColumn
                7, // maxColumn
                page);

        this.player = player;

        setUnregisterOnClose(true);

        setOutline(TrailMenuUtils.getOutlineSlot(), false);

        MenuSlot clearSlot = new MenuSlot(plugin, Material.RED_DYE, "&cClear Trails");
        clearSlot.setClickAction(event -> {
            TrailsController.getInstance().clearTrails(player);
            plugin.runSync(() -> {
                unregister();
                getPage(0).showTo(player); // Reload the whole menu since trails will be cleared
            });
        });

        setSlot(rows - 1, 4, TrailMenuUtils.getBuildLink());

        setSlot(0, 8, clearSlot);

        setSlot(0, 0, TrailMenuUtils.getMainMenuLink());
    }

    @Override
    protected PageSlot<Trail<?>> getSlot(Trail<?> trail) {
        Player player = trail.getPlayer(); // Get player since this can be called before constructor

        PageSlot<Trail<?>> trailSlot = TrailMenuUtils.getTrailPreview(trail, true);

        ItemMeta meta = trailSlot.getItem().getItemMeta();
        assert meta != null;

        List<String> lore = meta.getLore();
        if (lore == null)
            lore = new LinkedList<>();

        String lineBreak = "&b&m                          ";

        lore.add(0, lineBreak);

        lore.add(lineBreak);
        lore.add("&7Click to edit");

        if (player.hasPermission("wbstrails.command.preset.save")) {
            lore.add("&7Shift+Click to create preset");
        }

        lore.add("&cCtrl+Q to delete");

        lore.add(lineBreak);

        meta.setLore(plugin.colouriseAll(lore));

        trailSlot.getItem().setItemMeta(meta);

        trailSlot.setClickAction(event -> {
            ClickType type = event.getClick();
            switch (type) {
                case CONTROL_DROP:
                    TrailsController.getInstance().removeTrail(player, trail);
                    plugin.runSync(() -> {
                        unregister();
                        getPage(0).showTo(player); // Reload the whole menu since trails will be cleared
                    });
                    break;
                case DROP:
                    plugin.sendMessage("&wHold control when dropping to delete this trail!", player);
                    break;
                default:
                    if (type.isShiftClick() && player.hasPermission("wbstrails.command.preset.save")) {
                        createPreset(trail);
                        break;
                    }

                    openTrailEditMenu(trail);
                    break;
            }
        });

        return trailSlot;
    }

    private void createPreset(Trail<?> trail) {
        boolean registered = ChatStringReader.getStringFromChat(player, newName -> {
            PresetTrail<?> preset = trail.toPreset(newName);
            PresetManager.setPreset(preset.getId(), preset);
            plugin.sendMessage("Preset created! Id: " + preset.getId(), player);
        }, () -> plugin.sendMessage("&wPreset creation timed out.", player), 600);

        if (registered) {
            plugin.sendMessage("Type the name of your new preset in chat, or type &h\"" + ChatStringReader.CANCEL_PHRASE + "\"&r to cancel.", player);
            player.closeInventory();
        } else {
            plugin.sendMessage("Already pending! Type &h\"" + ChatStringReader.CANCEL_PHRASE + "\"&r to cancel it.", player);
        }
    }

    private <T extends Trail<T>> void openTrailEditMenu(Trail<T> trail) {
        //noinspection unchecked
        plugin.runSync(() ->
                new ChooseParticleMenu<>(WbsTrails.getInstance(), this, (T) trail, player).showTo(player));
    }

    @Override
    protected PagedMenu<Trail<?>> getPage(int page) {
        return new CurrentTrailsMenu(plugin, player, page);
    }

    @Override
    public @Nullable MenuPage getLastPage() {
        return null;
    }
}
