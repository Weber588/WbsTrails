package wbs.trails.command.preset;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.TrailsController;
import wbs.trails.TrailsSettings;
import wbs.trails.WbsTrails;
import wbs.trails.trails.Trail;
import wbs.trails.trails.presets.PresetManager;
import wbs.trails.trails.presets.PresetTrail;
import wbs.utils.util.commands.WbsSubcommand;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class PresetLoadCommand extends WbsSubcommand {
    public PresetLoadCommand(@NotNull WbsPlugin plugin) {
        super(plugin, "load");
        addAlias("use");
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

        String presetId = args[start];

        PresetTrail<?> preset = PresetManager.getPreset(presetId);

        if (preset == null) {
            sendMessage("Invalid preset: " + presetId + ". Please choose from the following: &h" +
                    PresetManager.getPresets().stream()
                            .filter(check -> sender.hasPermission(check.getPermission()))
                            .map(PresetTrail::getName)
                            .collect(Collectors.joining(", ")), sender);
            return true;
        }

        if (!sender.hasPermission(preset.getPermission())) {
            sendMessage("You don't have permission to use that trail.", sender);
            return true;
        }

        Player player = (Player) sender;
        Trail<?> trail = preset.getTrail(player);

        if (!TrailsController.getInstance().canHaveMore(player)) {
            sendMessage("&wYou cannot have any more trails. Use &h/trails clear&w or &h/trails remove&w to change trails!", player);
            return true;
        }

        if (!TrailsController.getInstance().addTrail(player, trail)) {
            sendMessage("You cannot add trails while your other trails are disabled.", player);
            return true;
        }
        trail.enable();

        return true;
    }

    @Override
    protected List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {
        if (args.length == start) {
            return PresetManager.getPresets().stream()
                    .filter(preset -> sender.hasPermission(preset.getPermission()))
                    .map(PresetTrail::getName)
                    .collect(Collectors.toList());
        }

        return null;
    }
}
