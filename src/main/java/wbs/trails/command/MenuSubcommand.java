package wbs.trails.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.utils.util.commands.WbsSubcommand;
import wbs.utils.util.menus.WbsMenu;

public abstract class MenuSubcommand extends WbsSubcommand {
    public MenuSubcommand(@NotNull WbsTrails plugin, @NotNull String label) {
        super(plugin, label);
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage("This command is only usable by players.", sender);
            return true;
        }
        Player player = (Player) sender;

        getMenu(player).showTo(player);

        return true;
    }

    protected abstract WbsMenu getMenu(Player player);
}