package wbs.trails.menus;

import wbs.trails.trails.TrailMenuUtils;
import wbs.utils.util.menus.WbsMenu;
import wbs.utils.util.plugin.WbsPlugin;

/**
 * The main menu, offering links to other menus for the following:
 * Presets:
 *      A paged menu of preset particle effects configured either in-game or from a config.
 *      Permissions can be set directly per preset (bypassing normal trail perms)
 *
 * Quick Build:
 *      A GUI-based trail builder that abstracts most of the specific configuration, only including
 *      Shape and Particle
 *
 * Build:
 *      A GUI-based
 */
public final class MainMenu extends WbsMenu {
    public MainMenu(WbsPlugin plugin) {
        super(plugin, "Trails", 3, "main_menu");

        setOutline(TrailMenuUtils.getOutlineSlot());

        setSlot(1, 1, TrailMenuUtils.getBuildLink());

        setSlot(1, 4, TrailMenuUtils.getBuildLink());

        setSlot(1, 7, TrailMenuUtils.getBuildLink());
    }
}
