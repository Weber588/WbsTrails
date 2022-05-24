package wbs.trails.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.menus.MainMenu;
import wbs.trails.menus.PlayerSpecificMenu;
import wbs.trails.menus.build.ChooseTrailMenu;
import wbs.utils.util.menus.WbsMenu;

public class MainMenuSubcommand extends MenuSubcommand {
    public MainMenuSubcommand(@NotNull WbsTrails plugin) {
        super(plugin, "menu");
    }

    @Override
    protected WbsMenu getMenu(Player player) {
        return new MainMenu(plugin);
    }
}
