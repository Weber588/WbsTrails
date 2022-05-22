package wbs.trails.command;

import wbs.trails.TrailsSettings;
import wbs.trails.WbsTrails;
import wbs.utils.util.commands.WbsErrorsSubcommand;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.plugin.WbsSettings;

public class ErrorsSubcommand extends WbsErrorsSubcommand {
    private final TrailsSettings settings;
    public ErrorsSubcommand(WbsTrails plugin) {
        super(plugin);
        settings = plugin.settings;
    }

    @Override
    protected WbsSettings getSettings() {
        return settings;
    }
}
