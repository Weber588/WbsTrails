package wbs.trails.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.menus.PlayerSpecificMenu;
import wbs.trails.menus.build.BuildMenu;

public class MainMenuSubcommand extends MenuSubcommand {
    public MainMenuSubcommand(@NotNull WbsTrails plugin) {
        super(plugin, "menu");
    }

    @Override
    protected PlayerSpecificMenu getMenu(Player player) {
        return new BuildMenu(plugin, player);
    }
}
