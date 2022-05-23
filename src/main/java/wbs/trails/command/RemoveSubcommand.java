package wbs.trails.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.trails.Trail;

import java.util.Collection;

public class RemoveSubcommand extends TrailsSubcommand {
    public RemoveSubcommand(@NotNull WbsTrails plugin) {
        super(plugin, "remove");
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage("This command is only usable by players.", sender);
            return true;
        }
        Player player = (Player) sender;

        if (args.length < 2) {
            sendMessage("Usage: &h/trails remove <index>&r. Find the index with &h/trails list&r.", player);
            return true;
        }

        Collection<Trail<?>> trails = controller.getTrails(player);

        if (trails.size() == 0) {
            sendMessage("You don't have any trails built! Create one with &h/trails add", sender);
            return true;
        }

        int removeIndex;
        try {
            removeIndex = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sendMessage("Please use an integer index from &h/trails list&r.", player);
            return true;
        }
        if (trails.size() < removeIndex) {
            sendMessage("That index is too high. Choose one from &h/trails list&r.", player);
            return true;
        }

        int checkIndex = 1;
        Trail<?> toRemove = null;
        for (Trail<?> activeTrail : trails) {
            if (checkIndex == removeIndex) {
                toRemove = activeTrail;
                break;
            }
            checkIndex++;
        }
        controller.removeTrail(player, toRemove);

        int newSize = controller.getTrails(player).size();

        if (newSize > 0) {
            sendMessage("Trail removed. You have &h" + newSize + "&r trail(s).", player);
        } else {
            sendMessage("Trail removed. You have no trails active.", player);
        }

        return true;
    }
}
