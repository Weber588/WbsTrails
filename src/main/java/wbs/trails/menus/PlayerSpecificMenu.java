package wbs.trails.menus;

import org.bukkit.entity.Player;
import wbs.utils.util.menus.WbsMenu;
import wbs.utils.util.plugin.WbsPlugin;

public class PlayerSpecificMenu extends WbsMenu {
    protected final Player player;

    public PlayerSpecificMenu(WbsPlugin plugin, String title, int rows, String id, Player player) {
        super(plugin, title, rows, id + ":" + player.getUniqueId());
        this.player = player;

        setUnregisterOnClose(true);
    }
}
