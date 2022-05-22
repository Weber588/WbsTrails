package wbs.trails.command;

import org.bukkit.command.PluginCommand;
import wbs.trails.WbsTrails;
import wbs.trails.command.preset.PresetCommandNode;
import wbs.utils.util.commands.WbsCommand;

public class TrailsCommand extends WbsCommand {
    public TrailsCommand(WbsTrails plugin, PluginCommand command) {
        super(plugin, command);

        String permission = "wbstrails.command";

        addSubcommand(new AddTrailCommand(plugin), permission + ".add");
        addSubcommand(new ClearSubcommand(plugin), permission + ".clear");
        addSubcommand(new HelpSubcommand(plugin), permission + ".help");
        addSubcommand(new ListSubcommand(plugin), permission + ".list");
        addSubcommand(new RemoveSubcommand(plugin), permission + ".list");
        addSubcommand(new SetTrailSubcommand(plugin), permission + ".set");
        addSubcommand(new ToggleSubcommand(plugin), permission + ".toggle");
        addSubcommand(new PresetCommandNode(plugin), permission + ".preset");

        // Menu commands
        addSubcommand(new MainMenuSubcommand(plugin), permission + ".menu");
        addSubcommand(new BuildSubcommand(plugin), permission + ".build");

        // Admin commands
        addSubcommand(new ErrorsSubcommand(plugin), permission + ".admin.reload");
        addSubcommand(new ReloadSubcommand(plugin), permission + ".admin.reload");

    }
}
