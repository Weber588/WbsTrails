package wbs.trails.menus.build;

import org.bukkit.entity.Player;
import wbs.trails.WbsTrails;
import wbs.trails.menus.TrailMenuUtils;
import wbs.trails.menus.build.options.ConfigOptionSlot;
import wbs.trails.trails.RegisteredTrail;
import wbs.trails.trails.Trail;
import wbs.trails.trails.options.ConfigOption;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.PagedMenu;
import wbs.utils.util.plugin.WbsPlugin;

public class TrailOptionsMenu<T extends Trail<T>> extends PagedMenu<ConfigOption<T, ?>> {

    private final Player player;
    private final RegisteredTrail<T> registration;

    public TrailOptionsMenu(WbsTrails plugin, RegisteredTrail<T> registration, Player player) {
        this(plugin, registration, player, 0);
    }

    public TrailOptionsMenu(WbsPlugin plugin, RegisteredTrail<T> registration, Player player, int page) {
        super(plugin,
                registration.getOptions(),
                "&3&lCustomize your trail!",
                "trail_option:" + player.getUniqueId(),
                1,
                4,
                1,
                7,
                page);
        this.player = player;
        this.registration = registration;

        setUnregisterOnClose(true);
        setOutline(TrailMenuUtils.getOutlineSlot());

        // TODO: Add "Done" slot that builds and enables the trail.
        //  (Maybe "create preset" button if the permission is set?)

        // TODO: Add "back" button
        //  (How to know if particle data menu was used?)
    }

    @Override
    protected MenuSlot getSlot(ConfigOption<T, ?> option) {
        return ConfigOptionSlot.toSlot(option);
    }

    @Override
    protected PagedMenu<ConfigOption<T, ?>> getPage(int page) {
        return new TrailOptionsMenu<>(plugin, registration, player, page);
    }
}
