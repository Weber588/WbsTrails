package wbs.trails.command.preset;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.TrailsController;
import wbs.trails.trails.Trail;
import wbs.trails.trails.presets.PresetGroup;
import wbs.trails.trails.presets.PresetManager;
import wbs.trails.trails.presets.PresetTrail;
import wbs.utils.util.commands.WbsSubcommand;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.string.WbsStrings;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        Player player = (Player) sender;

        String presetName = WbsStrings.combineLast(args, start, " ");

        List<Trail<?>> activeTrails = TrailsController.getInstance().getActiveTrails(player);
        if (activeTrails.isEmpty()) {
            sendMessage("You don't have any trails active right now! Enable a trail first.", sender);
            return true;
        }

        PresetGroup presetGroup = new PresetGroup(activeTrails.stream().map(Trail::toPreset).collect(Collectors.toList()), presetName, presetName);

        Material material = player.getInventory().getItemInMainHand().getType();
        if (material != Material.AIR) {
            presetGroup.setMaterial(material);
        }

        PresetManager.setPreset(presetName, presetGroup);

        sendMessage("Preset created! You can load it with &h"
                + getAlternativeCommand("load", label, args, start) + " "
                + presetName + "&r.", sender);

        return true;
    }

    private String getAlternativeCommand(String alternative, String label, String[] args, int start) {
        return "/" + label + " " + String.join(" ", Arrays.copyOfRange(args, 0, start - 1)) + " " + alternative;
    }
}
