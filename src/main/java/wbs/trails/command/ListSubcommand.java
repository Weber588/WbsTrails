package wbs.trails.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.trails.Trail;

import java.util.Collection;

public class ListSubcommand extends TrailsSubcommand {
    public ListSubcommand(@NotNull WbsTrails plugin) {
        super(plugin, "list");
        addAlias("active");
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage("This command is only usable by players.", sender);
            return true;
        }
        Player player = (Player) sender;

        Collection<Trail<?>> trails = controller.getTrails(player);

        if (trails.size() == 0) {
            sendMessage("You don't have any trails built! Create one with &h/trails add", sender);
            return true;
        }

        int index = 1;
        for (Trail<?> activeTrail : trails) {
            sendMessage("&h" + index + ")&r " + activeTrail.getRegistration().getName(), player);
            index++;
        }

        return true;
    }
}
