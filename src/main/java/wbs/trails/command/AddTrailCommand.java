package wbs.trails.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.TrailsController;
import wbs.trails.WbsTrails;
import wbs.trails.trails.Trail;

import java.util.Collection;
import java.util.List;

public class AddTrailCommand extends TrailsSubcommand {
    public AddTrailCommand(@NotNull WbsTrails plugin) {
        super(plugin, "add");
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage("This command is only usable by players.", sender);
            return true;
        }
        Player player = (Player) sender;

        TrailsController controller = TrailsController.getInstance();
        Trail<?> trail = buildTrail(player, args);
        controller.tryAddTrail(player, trail);

        return true;
    }

    @Override
    protected List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        return getTrailTabCompletions(sender, label, args);
    }
}
