package wbs.trails.menus.build;

import org.bukkit.entity.Player;
import wbs.trails.menus.PlayerSpecificMenu;
import wbs.trails.menus.TrailMenuUtils;
import wbs.utils.util.plugin.WbsPlugin;

public class DataOptionsMenu extends PlayerSpecificMenu {
    public DataOptionsMenu(WbsPlugin plugin, String title, int rows, String id, Player player) {
        super(plugin, title, rows, id, player);

        setOutline(TrailMenuUtils.getOutlineSlot());
    }
}
