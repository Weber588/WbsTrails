package wbs.trails.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.trails.Trail;

import java.util.List;

public class SetTrailSubcommand extends TrailsSubcommand {
    public SetTrailSubcommand(@NotNull WbsTrails plugin) {
        super(plugin, "set");
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage("This command is only usable by players.", sender);
            return true;
        }
        Player player = (Player) sender;

        Trail<?> trail = buildTrail(player, args);
        if (trail == null) {
            return true;
        }

        controller.clearTrails(player);
        controller.addTrail(player, trail);
        trail.enable();

        sendMessage("Trail set.", player);

        return true;
    }

    @Override
    protected List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        return getTrailTabCompletions(sender, label, args);
    }
}
