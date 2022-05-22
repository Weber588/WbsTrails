package wbs.trails.command;

import wbs.trails.TrailsSettings;
import wbs.trails.WbsTrails;
import wbs.utils.util.commands.WbsReloadSubcommand;
import wbs.utils.util.plugin.WbsSettings;

public class ReloadSubcommand extends WbsReloadSubcommand {
    private final TrailsSettings settings;
    public ReloadSubcommand(WbsTrails plugin) {
        super(plugin);
        settings = plugin.settings;
    }

    @Override
    protected WbsSettings getSettings() {
        return settings;
    }
}
