package wbs.trails;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import wbs.utils.util.plugin.WbsMessenger;

@SuppressWarnings("unused")
public class CombatListener extends WbsMessenger implements Listener {

	private final TrailsSettings settings;
	public CombatListener(WbsTrails plugin) {
		super(plugin);
		settings = plugin.settings;
	}

	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		if (!settings.toggleOnPlayerDamage()) {
			return;
		}
		
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}
		
		Player victim = (Player) entity;
		
		Entity attackingEntity = event.getDamager();
		if (!(attackingEntity instanceof Player)) {
			return;
		}
		
		Player attacker = (Player) attackingEntity;
		
		if (attacker.equals(victim)) {
			return;
		}
		
		TrailsController controller = TrailsController.getInstance();
		
		if (controller.hasActiveTrails(victim)) {
			controller.toggle(victim);
			sendMessage("&wNow in combat! Your trails have been disabled.", victim);
		}

		if (controller.hasActiveTrails(attacker)) {
			controller.toggle(attacker);
			sendMessage("&wNow in combat! Your trails have been disabled.", attacker);
		}
	}
	

	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!settings.toggleOnMobDamage()) {
			return;
		}
		
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}
		
		Player victim = (Player) entity;
		
		Entity attackingEntity = event.getDamager();
		if (attackingEntity instanceof Player) {
			return;
		}

		TrailsController controller = TrailsController.getInstance();
		
		if (controller.hasActiveTrails(victim)) {
			controller.toggle(victim);
			sendMessage("&wNow in combat! Your trails have been disabled.", victim);
		}
	}

	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
	public void onAnyDamage(EntityDamageEvent event) {
		if (!settings.toggleOnAllDamage()) {
			return;
		}
		
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}
		
		Player victim = (Player) entity;
		
		TrailsController controller = TrailsController.getInstance();
		
		if (controller.hasActiveTrails(victim)) {
			controller.toggle(victim);
			sendMessage("&wNow in combat! Your trails have been disabled.", victim);
		}
	}
}
