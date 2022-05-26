package wbs.trails.menus.build;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wbs.trails.TrailsController;
import wbs.trails.menus.TrailMenuUtils;
import wbs.trails.menus.build.options.ConfigOptionSlot;
import wbs.trails.trails.Trail;
import wbs.trails.trails.options.ConfigOption;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.PagedMenu;
import wbs.utils.util.menus.WbsMenu;
import wbs.utils.util.plugin.WbsPlugin;

public class TrailOptionsMenu<T extends Trail<T>> extends PagedMenu<ConfigOption<T, ?>> implements BuildMenu {

    private final BuildMenu lastPage;
    private final Player player;
    private final T trail;

    public TrailOptionsMenu(WbsPlugin plugin, BuildMenu lastPage, T trail, Player player) {
        this(plugin, lastPage, trail, player, 0);
    }

    public TrailOptionsMenu(WbsPlugin plugin, BuildMenu lastPage, T trail, Player player, int page) {
        super(plugin,
                trail.getRegistration().getOptions(),
                "&3&lCustomize your trail!",
                "trail_option:" + player.getUniqueId(),
                1, // rowStart
                4, // maxRows
                1, // minColumn
                7, // maxColumn
                page);

        this.lastPage = lastPage;
        this.player = player;
        this.trail = trail;

        setUnregisterOnClose(true);

        setOutline(TrailMenuUtils.getOutlineSlot());

        MenuSlot doneSlot = new MenuSlot(plugin, Material.LIME_DYE, "&a&lClick to finish!");

        doneSlot.setClickAction(event -> {
            updateTrail();

            TrailsController controller = TrailsController.getInstance();
            if (controller.tryAddTrail(player, trail)) {
                trail.enable();

                plugin.sendMessage("Trail enabled!", player);
                player.closeInventory();
                unregister();
            }
        });

        setSlot(rows - 1, 7, doneSlot);

        setSlot(rows - 1, 1, getBackSlot());
    }

    private void updateTrail() {
        for (MenuSlot slot : pageSlots) {
            //noinspection unchecked
            ((ConfigOptionSlot<T, ?>) slot).getPair().apply(trail);
        }
    }

    @Override
    protected MenuSlot getSlot(ConfigOption<T, ?> option) {
        return option.newSlot();
    }

    @Override
    protected PagedMenu<ConfigOption<T, ?>> getPage(int page) {
        return new TrailOptionsMenu<>(plugin, lastPage, trail, player, page);
    }

    @Override
    public @Nullable BuildMenu getLastPage() {
        return lastPage;
    }
}
