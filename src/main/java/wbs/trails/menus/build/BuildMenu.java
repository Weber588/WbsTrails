package wbs.trails.menus.build;

import org.bukkit.entity.Player;
import wbs.trails.menus.PlayerSpecificMenu;
import wbs.trails.trails.TrailMenuUtils;
import wbs.utils.util.plugin.WbsPlugin;

public class BuildMenu extends PlayerSpecificMenu {
    public BuildMenu(WbsPlugin plugin, Player player) {
        super(plugin, "Choose a shape", 4, "build", player);

        setOutline(TrailMenuUtils.getOutlineSlot());

    }
}
