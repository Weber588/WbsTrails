package wbs.trails.command.preset;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.TrailsController;
import wbs.trails.trails.Trail;
import wbs.trails.trails.presets.PresetManager;
import wbs.trails.trails.presets.PresetTrail;
import wbs.utils.util.commands.WbsSubcommand;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.Arrays;
import java.util.List;

public class PresetSaveCommand extends WbsSubcommand {
    public PresetSaveCommand(@NotNull WbsPlugin plugin) {
        super(plugin, "save");
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {
        if (!(sender instanceof Player)) {
            sendMessage("This command is only usable by players.", sender);
            return true;
        }

        if (args.length <= start) {
            sendUsage("<id>", sender, label, args);
            return true;
        }

        if (args.length > start + 1) {
            sendMessage("Preset id must contain no spaces. " +
                    "To assign a dynamic name, you can change it after creation with " +
                    "&h" + getAlternativeCommand("rename", label, args, start) +
                    "&r.", sender);
            return true;
        }

        Player player = (Player) sender;

        String presetId = args[start];

        List<Trail<?>> activeTrails = TrailsController.getInstance().getActiveTrails(player);
        if (activeTrails.isEmpty()) {
            sendMessage("You don't have any trails active right now! Enable a trail first.", sender);
            return true;
        }

        if (activeTrails.size() > 1) {
            sendMessage("You have multiple trails active! Disable " + (activeTrails.size() - 1) + " trails first.", sender);
            return true;
        }

        Trail<?> trail = activeTrails.get(0);
        PresetTrail<?> preset = trail.toPreset(presetId);
        PresetManager.setPreset(presetId, preset);

        sendMessage("Preset created! You can load it with &h" + getAlternativeCommand("load", label, args, start) + " " + presetId + "&r.", sender);

        return true;
    }

    private String getAlternativeCommand(String alternative, String label, String[] args, int start) {
        return "/" + label + " " + String.join(" ", Arrays.copyOfRange(args, 0, start - 1)) + " " + alternative;
    }
}
