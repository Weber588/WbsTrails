package wbs.trails;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import wbs.trails.command.TrailsCommand;
import wbs.trails.trails.Trail;
import wbs.trails.trails.TrailManager;
import wbs.trails.trails.presets.PresetManager;
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
			getDataFolder().mkdir();
		}

		TrailManager.registerNativeTrails();
		
		settings = new TrailsSettings(this);
		settings.reload();
		
		Trail.setPlugin(this);
		TrailsController.getInstance().startTimers();

		new TrailsCommand(this, getCommand("trails"));
		
	    PluginManager pm = Bukkit.getServer().getPluginManager();
	    pm.registerEvents(new CombatListener(this), this);

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

    	settings.savePresets();
    }
}
