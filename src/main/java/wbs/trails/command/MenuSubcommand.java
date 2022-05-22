package wbs.trails.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.menus.PlayerSpecificMenu;
import wbs.utils.util.commands.WbsSubcommand;
import wbs.utils.util.menus.WbsMenu;
import wbs.utils.util.plugin.WbsPlugin;

public abstract class MenuSubcommand extends WbsSubcommand {
    public MenuSubcommand(@NotNull WbsTrails plugin, @NotNull String label) {
        super(plugin, label);
    }

    private static final boolean WORK_IN_PROGRESS = true;

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (WORK_IN_PROGRESS) {
            sendMessage("Work in progress.", sender);
        } else {
            if (!(sender instanceof Player)) {
                sendMessage("This command is only usable by players.", sender);
                return true;
            }
            Player player = (Player) sender;

            getMenu(player).showTo(player);
        }

        return true;
    }

    protected abstract PlayerSpecificMenu getMenu(Player player);
}