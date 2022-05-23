package wbs.trails;

import java.time.LocalDateTime;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import wbs.trails.trails.*;

public class TrailsController {
	
	private static TrailsController instance = null;
	public static TrailsController getInstance() {
		if (instance == null) {
			instance = new TrailsController();
		}
		return instance;
	}

	private WbsTrails plugin;
	private final TrailsSettings settings;
	
	private TrailsController() {
		plugin = WbsTrails.getInstance();
		settings = plugin.settings;
	}
	
	private final Map<Player, Boolean> hasActiveTrails = new HashMap<>();
	public boolean hasActiveTrails(Player player) {
		if (hasActiveTrails.containsKey(player)) {
			return hasActiveTrails.get(player);
		}
		
		return false;
	}

	public boolean canHaveMore(Player player) {
		for (int i = getActiveTrails(player).size() + 1; i <= settings.getMaxMultiple(); i++) {
			if (player.hasPermission("wbstrails.multiple." + i)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Toggle the players trails.
	 * @param player The player to toggle
	 * @return True if the trails were enabled, false if disabled.
	 */
	public boolean toggle(Player player) {
		if (hasActiveTrails(player)) {
			hasActiveTrails.put(player, false);
			for (Trail<?> trail : playerTrails.get(player)) {
				trail.disable();
			}
			return false;
		} else {
			hasActiveTrails.put(player, true);
			for (Trail<?> trail : playerTrails.get(player)) {
				trail.enable();
			}
			return true;
		}
	}

	private final Multimap<Player, Trail<?>> playerTrails = ArrayListMultimap.create();
	public Collection<Trail<?>> getTrails(Player player) {
		return new LinkedList<>(playerTrails.get(player));
	}
	/**
	 * Add another trail to this player.
	 * @param player The player who owns the trail
	 * @param trail The trail to add
	 * @return True if the trail was added, false if the player's trails
	 * were disabled.
	 */
	public boolean addTrail(Player player, Trail<?> trail) {
		if (!hasActiveTrails(player) && getTrails(player).size() > 0) {
			return false;
		}
		playerTrails.put(player, trail);
		hasActiveTrails.put(player, true);
		return true;
	}
	
	public void removeTrail(Player player, Trail<?> trail) {
		playerTrails.remove(player, trail);
		trail.disable();
		if (playerTrails.get(player).size() == 0) {
			hasActiveTrails.put(player, false);
		}
	}
	
	public void clearTrails(Player player) {
		playerTrails.removeAll(player);
		hasActiveTrails.put(player, false);
	}
	
	private int timerID = 0;
	
	public void startTimers() {
		if (timerID != 0) { 
			Bukkit.getScheduler().cancelTask(timerID);
		}
		timerID = new BukkitRunnable() {
			@Override
			public void run() {
				for (Trail<?> trail : playerTrails.values()) {
					if (trail.isActive()) {
						if (!trail.getPlayer().isOnline()) {
							trail.disable();
							continue;
						}

						trail.tick();
					}
				}
			}
		}.runTaskTimer(plugin, 0L, settings.getRefreshRate()).getTaskId();
	}
	
	private final Map<Player, LocalDateTime> lastHits = new HashMap<>();
	public void setLastHit(Player player) {
		lastHits.put(player, LocalDateTime.now());
	}
	
	public void removeLastHit(Player player) {
		lastHits.remove(player);
	}
	
	public LocalDateTime getLastHit(Player player) {
		return lastHits.get(player);
	}

	public List<Trail<?>> getActiveTrails(Player player) {
		return new LinkedList<>(playerTrails.get(player));
	}
}
