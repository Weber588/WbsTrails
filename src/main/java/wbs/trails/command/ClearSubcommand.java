package wbs.trails.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;

import java.util.LinkedList;
import java.util.List;

public class ClearSubcommand extends TrailsSubcommand {
    public ClearSubcommand(@NotNull WbsTrails plugin) {
        super(plugin, "clear");
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage("This command is only usable by players.", sender);
            return true;
        }
        Player player = (Player) sender;

        if (args.length > 1) {
            if (!checkPermission(player, "wbstrails.admin.clearother")) {
                return true;
            }

            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer != null) {
                controller.clearTrails(targetPlayer);
                sendMessage("Your trails were cleared by a staff member.", targetPlayer);
            } else {
                sendMessage("&wPlayer not found.", player);
                return true;
            }
        } else {
            controller.clearTrails(player);
        }

        sendMessage("Trails cleared.", player);

        return true;
    }

    @Override
    protected List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        List<String> choices = new LinkedList<>();

        if (args.length == 2) {
            if (sender.hasPermission("wbstrails.admin.clearother")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    choices.add(player.getName());
                }
            }
        }

        return choices;
    }
}
