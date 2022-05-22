package wbs.trails.command.preset;

import wbs.utils.util.commands.WbsCommandNode;
import wbs.utils.util.plugin.WbsPlugin;

public class PresetCommandNode extends WbsCommandNode {
    public PresetCommandNode(WbsPlugin plugin) {
        super(plugin, "preset");
        addAlias("presets");

        addChild(new PresetSaveCommand(plugin), getPermission() + ".save");
        addChild(new PresetLoadCommand(plugin), getPermission() + ".load");
        addChild(new PresetListCommand(plugin), getPermission() + ".list");
    }
}
