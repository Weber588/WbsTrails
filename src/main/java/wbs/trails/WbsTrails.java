package wbs.trails;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wbs.trails.command.TrailsCommand;
import wbs.trails.listeners.ChatStringReader;
import wbs.trails.listeners.CombatListener;
import wbs.trails.trails.Trail;
import wbs.trails.trails.TrailManager;
import wbs.utils.util.plugin.WbsPlugin;

public class WbsTrails extends WbsPlugin {
	
	private static WbsTrails instance;
	public static WbsTrails getInstance() {
		return instance;
	}
	
	public TrailsSettings settings;
	
	@Override
	public void onEnable() {
		instance = this;
		if (!getDataFolder().exists()) {
			if (!getDataFolder().mkdir()) {
				pluginManager.disablePlugin(this);
				logger.severe("Failed to create data folder.");
				return;
			}
		}

		TrailManager.registerNativeTrails();
		
		settings = new TrailsSettings(this);
		settings.reload();
		
		Trail.setPlugin(this);
		TrailsController.getInstance().startTimers();

		new TrailsCommand(this, getCommand("trails"));

		registerListener(new CombatListener(this));
	    registerListener(new ChatStringReader());

	}

    @Override
    public void onDisable() {
    	TrailsController controller = TrailsController.getInstance();
    	for (Player player : Bukkit.getOnlinePlayers()) {
    		if (controller.hasActiveTrails(player)) {
        		controller.clearTrails(player);
        		sendMessage("Plugin is disabling; your trails have been toggled.", player);
    		}
    	}
    }
}
