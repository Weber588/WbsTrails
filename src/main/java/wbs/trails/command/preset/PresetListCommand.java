package wbs.trails.command.preset;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.commands.WbsSubcommand;
import wbs.utils.util.plugin.WbsPlugin;

public class PresetListCommand extends WbsSubcommand {
    public PresetListCommand(@NotNull WbsPlugin plugin) {
        super(plugin, "list");
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {
        sendMessage("&wWork in Progress.", sender);
        return true;
    }
}
