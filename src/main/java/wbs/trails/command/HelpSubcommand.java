package wbs.trails.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.commands.WbsSubcommand;
import wbs.utils.util.plugin.WbsPlugin;

public class HelpSubcommand extends WbsSubcommand {
    public HelpSubcommand(@NotNull WbsPlugin plugin) {
        super(plugin, "help");
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        // TODO: Move this to be dynamic with annotations in WbsUtils
        sendMessage("&h/trails help&r: Show this help message.", sender);
        sendMessage("&h/trails add <type> <particle> [options...]&r: Create and add the trail to your active ones.", sender);
        sendMessage("&h/trails set <type> <particle> [options...]&r: Create your trail and remove all active trails.", sender);
        sendMessage("&h/trails clear&r: Remove all trails.", sender);
        sendMessage("&h/trails toggle&r: Enable or disable your trails.", sender);
        sendMessage("&h/trails remove <index>&r: Remove a trail at a given index.", sender);
        sendMessage("&h/trails list&r: Show a list of your trails.", sender);

        if (sender.hasPermission("wbstrails.admin.reload")) {
            sendMessage("&h/trails reload&r: Reload the config.", sender);
            sendMessage("&h/trails errors&r: If the reload had errors, view them here to help you fix any config issues.", sender);
        }

        return true;
    }
}
