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

	private final WbsTrails plugin;
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

	public int getMaxTrails(Player player) {
		for (int i = settings.getMaxMultiple() + 1; i > 0; i--) {
			if (player.hasPermission("wbstrails.multiple." + i)) {
				return i;
			}
		}

		return 1;
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

	/**
	 * Try to add a trail to a player, sending errors to that player if anything goes wrong.
	 * @param player The player to register the trail for, and to send errors to.
	 * @param trail The trail to register to the given player
	 * @return True if the trail was registered successfully and no message was sent, false if
	 * an error occurred and the player was already messaged.
	 */
	public boolean tryAddTrail(Player player, Trail<?> trail) {
		if (!player.hasPermission(trail.getRegistration().getPermission())) {
			plugin.sendMessage("&wYou don't have permission to use that trail.", player);
			return false;
		}

		int maxTrails = getMaxTrails(player);

		if (maxTrails > 1 && getActiveTrails(player).size() >= maxTrails) {
				plugin.sendMessage("&wYou cannot have any more trails. Use &h/trails clear&w or &h/trails remove&w to change trails!", player);
			return false;
		}

		if (maxTrails > 1) {
			if (!addTrail(player, trail)) {
				plugin.sendMessage("You cannot add trails while your other trails are disabled.", player);
				return false;
			}
		} else {
			// Player can only have one trail; treat add as set.
			clearTrails(player);
			addTrail(player, trail);
		}

		trail.enable();

		Collection<Trail<?>> trails = getTrails(player);
		plugin.sendMessage("Trail added. You have &h" + trails.size() + "&r trails active.", player);
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

	public List<Trail<?>> getActiveTrails(Player player) {
		return new LinkedList<>(playerTrails.get(player));
	}
}
