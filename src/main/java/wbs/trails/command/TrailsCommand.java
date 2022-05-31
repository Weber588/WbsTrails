package wbs.trails.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.command.preset.PresetCommandNode;
import wbs.trails.menus.CurrentTrailsMenu;
import wbs.trails.menus.TrailMenuUtils;
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

    @Override
    public boolean onCommandNoArgs(@NotNull CommandSender sender, String label) {
        if (!(sender instanceof Player)) {
            return super.onCommandNoArgs(sender, label);
        }

        // TODO: Make this it configurable to do menus by default

        Player player = (Player) sender;

        if (label.equalsIgnoreCase("trails")) {
            new CurrentTrailsMenu(plugin, player).showTo(player);
        } else {
            TrailMenuUtils.getMainMenu().showTo(player);
        }
        return true;
    }
}
