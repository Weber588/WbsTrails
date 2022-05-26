package wbs.trails.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.menus.build.ChooseTrailMenu;
import wbs.utils.util.menus.WbsMenu;

public class BuildSubcommand extends MenuSubcommand {
    public BuildSubcommand(@NotNull WbsTrails plugin) {
        super(plugin, "build");
    }

    @Override
    protected WbsMenu getMenu(Player player) {
        return new ChooseTrailMenu(plugin, player);
    }
}
