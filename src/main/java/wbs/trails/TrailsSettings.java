package wbs.trails;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.entity.Player;
import wbs.trails.trails.*;
import wbs.trails.trails.presets.PresetManager;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.plugin.WbsSettings;

public class TrailsSettings extends WbsSettings {

	public TrailsSettings(WbsPlugin plugin) {
		super(plugin);
	}

	private YamlConfiguration config;
	
	@Override
	public void reload() {
		errors.clear();
		
		genConfig("config.yml");
		
		File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) { 
        	plugin.saveResource("config.yml", false);
        }
        config = loadConfigSafely(configFile);

        loadMessageFormat(config);
		loadSettings();

		loadPresets();
	}
	
	private void loadSettings() {
		refreshRate = config.getInt("refresh-rate", refreshRate);
		maxMultiple = config.getInt("max-multiple", maxMultiple);
		
		ConfigurationSection pvpSection = config.getConfigurationSection("toggle-on-damage");
		if (pvpSection != null) {
			toggleOnPlayerDamage = pvpSection.getBoolean("toggle-from-players");
			toggleOnMobDamage = pvpSection.getBoolean("toggle-from-mobs");
			toggleOnAllDamage = pvpSection.getBoolean("toggle-from-all");
			
			pvpCooldown = pvpSection.getInt("disable-duration");
		}
		
		String directory = "config.yml/";

		allowedParticles.clear();
		allowedParticles.addAll(Arrays.asList(Particle.values()));

		List<String> blacklistParticleNames = config.getStringList("particle-blacklist");

		if (blacklistParticleNames.isEmpty()) {
			plugin.logger.warning("The particle-blacklist section was missing.");
		} else {
			for (String asString : blacklistParticleNames) {
				Particle particle = WbsEnums.getEnumFromString(Particle.class, asString);
				if (particle != null) {
					allowedParticles.remove(particle);
					particleBlacklist.add(particle);
				} else {
					logError("Invalid or outdated/future particle. (" + asString + ")", directory + "particle-blacklist");
				}
			}
		}

		for (Particle blacklistParticle : forceBlacklistedParticles) {
			allowedParticles.remove(blacklistParticle);
		}

		ConfigurationSection particleSetConfigs = config.getConfigurationSection("particle-sets");
		if (particleSetConfigs != null) {
			for (String setName : particleSetConfigs.getKeys(false)) {
				Set<Particle> newSet = new HashSet<>();
				List<String> particleNames = particleSetConfigs.getStringList(setName);
				for (String particleName : particleNames) {
					Particle particle = WbsEnums.particleFromString(particleName);
					if (particle != null) {
						newSet.add(particle);
					} else {
						logError("Invalid or outdated/future particle. (" + particleName + ")", directory + "particle-sets");
					}
				}
				if (!newSet.isEmpty()) particleSets.put(setName, newSet);
			}
		}
		
		ConfigurationSection trailsSettings = config.getConfigurationSection("trails-options");
		if (trailsSettings != null) {
			for (String trailName : trailsSettings.getKeys(false)) {
				if (!trailsSettings.isConfigurationSection(trailName)) {
					logError("Trail definition must be a section.", directory + "/trails-options/" + trailName);
					continue;
				}

				ConfigurationSection typeSection = trailsSettings.getConfigurationSection(trailName);
				if (typeSection != null) {
					RegisteredTrail<?> trail = TrailManager.getRegisteredTrail(trailName);

					if (trail == null) {
						logError("Trail not found: " + trailName, directory + "/trails-options/" + trailName);
						continue;
					}

					trail.configure(typeSection);
				}
			}
		}
	}

	private final String presetsFileName = "presets.yml";

	public void loadPresets() {
		File file = new File(this.plugin.getDataFolder(), presetsFileName);

		YamlConfiguration presetsConfig;

		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					plugin.logger.info(presetsFileName + " failed to create.");
					return;
				}
			} catch (IOException e) {
				plugin.logger.info(presetsFileName + " failed to create.");
				e.printStackTrace();
				return;
			}
		}
		presetsConfig = this.loadConfigSafely(file);

		PresetManager.loadPresets(presetsConfig, presetsFileName);
	}

	public void savePresets() {
		YamlConfiguration presetsConfig = loadConfigSafely(new File(plugin.getDataFolder(), presetsFileName));
		saveYamlData(presetsConfig, presetsFileName, "preset", PresetManager::savePresets);
	}
	
	private int pvpCooldown = 30;
	public int getPvpCooldown() {
		return pvpCooldown;
	}

	private boolean toggleOnPlayerDamage = false;
	public boolean toggleOnPlayerDamage() {
		return toggleOnPlayerDamage;
	}
	
	private boolean toggleOnMobDamage = false;
	public boolean toggleOnMobDamage() {
		return toggleOnMobDamage;
	}
	
	private boolean toggleOnAllDamage = false;
	public boolean toggleOnAllDamage() {
		return toggleOnAllDamage;
	}
	
	private final List<Particle> forceBlacklistedParticles = Arrays.asList(
			Particle.LEGACY_BLOCK_CRACK,
			Particle.LEGACY_BLOCK_DUST,
			Particle.LEGACY_FALLING_DUST,
			Particle.MOB_APPEARANCE
			);

	private final List<Particle> particleBlacklist = new LinkedList<>();
	
	private final Set<Particle> allowedParticles = new HashSet<>();
	public Set<Particle> getAllowedParticles() {
		return allowedParticles;
	}

	private final Map<String, Set<Particle>> particleSets = new HashMap<>();

	public Set<Particle> getAllowedParticlesFor(Player player) {
		Set<Particle> playerSet = new HashSet<>();

		if (player.hasPermission("wbstrails.particles.all")) {
			playerSet.addAll(allowedParticles);
		} else {
			for (String setName : particleSets.keySet()) {
				if (player.hasPermission("wbstrails.particles." + setName)) {
					playerSet.addAll(particleSets.get(setName));
				}
			}
		}

		for (String setName : particleSets.keySet()) {
			String permission = "wbstrails.particles." + setName;
			if (player.isPermissionSet(permission)) {
				if (!player.hasPermission(permission)) {
					playerSet.removeAll(particleSets.get(setName));
				}
			}
		}

		return playerSet;
	}

	public Set<Particle> getParticleBlacklist() {
		Set<Particle> returnSet = new HashSet<>(particleBlacklist);
		returnSet.addAll(forceBlacklistedParticles);
		return returnSet;
	}

	private int refreshRate = 5;
	public int getRefreshRate() {
		return refreshRate;
	}

	private int maxMultiple = 10;
	public int getMaxMultiple() {
		return maxMultiple;
	}

}
