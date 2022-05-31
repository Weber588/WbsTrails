package wbs.trails.menus;

import wbs.utils.util.menus.WbsMenu;
import wbs.utils.util.plugin.WbsPlugin;

public final class MainMenu extends WbsMenu {
    public MainMenu(WbsPlugin plugin) {
        super(plugin, "Trails", 3, "main_menu");

        setOutline(TrailMenuUtils.getOutlineSlot());

        setSlot(1, 2, TrailMenuUtils.getBuildLink());

        setSlot(1, 4, TrailMenuUtils.getCurrentTrailsLink());

        setSlot(1, 6, TrailMenuUtils.getPresetsLink());
    }
}
