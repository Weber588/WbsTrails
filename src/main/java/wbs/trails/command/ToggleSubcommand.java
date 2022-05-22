package wbs.trails.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.trails.Trail;

import java.util.Collection;

public class ToggleSubcommand extends TrailsSubcommand {
    public ToggleSubcommand(@NotNull WbsTrails plugin) {
        super(plugin, "toggle");
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage("This command is only usable by players.", sender);
            return true;
        }
        Player player = (Player) sender;

        Collection<Trail<?>> trails = controller.getTrails(player);

        int size = trails.size();

        if (size == 0) {
            sendMessage("You don't have a trail built! Create one with &h/trails add", sender);
            return true;
        }

        boolean enabled = controller.toggle(player);

        if (enabled) {
            sendMessage("Enabled &h" + size + "&r trail(s).", player);
        } else {
            sendMessage("Disabled &h" + size + "&r trail(s).", player);
        }
        return true;
    }
}
