package wbs.trails;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import wbs.trails.trails.*;
import wbs.trails.trails.options.ConfigOption;
import wbs.trails.trails.options.DoubleOption;
import wbs.trails.trails.presets.PresetManager;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.WbsMath;
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
		loadCustomTrails();

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
		
		String directory = "config.yml";

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

	private void loadCustomTrails() {
		String fileName = "custom_trails.yml";

		YamlConfiguration customTrailConfig = loadConfigSafely(genConfig(fileName));

		TrailManager.unregisterCustomTrails();

		int customTrailsLoaded = 0;
		customTrails:
		for (String customTrailName : customTrailConfig.getKeys(false)) {
			String directory = fileName + "/" + customTrailName;
			if (!customTrailConfig.isConfigurationSection(customTrailName)) {
				logError("Custom trail must be a section.", directory);
				continue;
			}

			ConfigurationSection section = customTrailConfig.getConfigurationSection(customTrailName);
			assert section != null;

			CustomRegisteredTrail registration = new CustomRegisteredTrail(customTrailName);

			String description = section.getString("description");
			if (description != null) {
				registration.setDescription(description);
			}

			String materialString = section.getString("material");
			if (materialString != null) {
				Material material = WbsEnums.materialFromString(materialString);
				if (material == null) {
					logError("Invalid material: " + materialString,  directory + "/material");
					continue;
				}

				registration.setMaterial(material);
			}

			if (!section.isList("points")) {
				logError("points must be a list of coordinates relative to the player.", directory + "/points");
				continue;
			}

			List<String> pointStrings = section.getStringList("points");
			List<Vector> points = new LinkedList<>();

			int index = 0;
			for (String pointString : pointStrings) {
				index++; // Natural indexing is fine; just for display
				String[] args = pointString.replaceAll("\\s", "")
						.split(",");

				if (args.length != 3) {
					logError("Points must contain 3 coordinates: 'x, y, z'. Invalid coordinates: " + pointString, directory + "/points:" + index);
					continue customTrails;
				}

				double x, y, z;
				try {
					x = Double.parseDouble(args[0]);
					y = Double.parseDouble(args[1]);
					z = Double.parseDouble(args[2]);
				} catch (NumberFormatException e) {
					logError("Invalid coordinates: " + pointString, directory + "/points:" + index);
					continue customTrails;
				}

				points.add(new Vector(x, y, z));
			}

			boolean flipX = section.getBoolean("flip.x", false);
			boolean flipY = section.getBoolean("flip.y", false);
			boolean flipZ = section.getBoolean("flip.z", false);

			if (flipX) {
				points.forEach(point -> point.setX(-point.getX()));
			}
			if (flipY) {
				points.forEach(point -> point.setY(-point.getY()));
			}
			if (flipZ) {
				points.forEach(point -> point.setZ(-point.getZ()));
			}

			double offsetX = section.getDouble("offset.x", 0);
			double offsetY = section.getDouble("offset.y", 0);
			double offsetZ = section.getDouble("offset.z", 0);

			if (offsetX != 0 || offsetY != 0 || offsetZ != 0) {
				Vector offset = new Vector(offsetX, offsetY, offsetZ);
				points.forEach(point -> point.add(offset));
			}

			double rotateX = section.getDouble("rotate.x", 0);
			double rotateY = section.getDouble("rotate.y", 0);
			double rotateZ = section.getDouble("rotate.z", 0);

			if (rotateX != 0) {
				points = WbsMath.rotateVectors(points, new Vector(1, 0, 0), rotateX);
			}
			if (rotateY != 0) {
				points = WbsMath.rotateVectors(points, new Vector(0, 1, 0), rotateY);
			}
			if (rotateZ != 0) {
				points = WbsMath.rotateVectors(points, new Vector(0, 0, 1), rotateZ);
			}

			boolean mirrorX = section.getBoolean("mirror.x", false);
			boolean mirrorY = section.getBoolean("mirror.y", false);
			boolean mirrorZ = section.getBoolean("mirror.z", false);

			if (mirrorX) {
				points.addAll(points.stream()
						.map(Vector::clone)
						.map(vector -> vector.setX(-vector.getX()))
						.collect(Collectors.toList())
				);
			}
			if (mirrorY) {
				points.addAll(points.stream()
						.map(Vector::clone)
						.map(vector -> vector.setY(-vector.getY()))
						.collect(Collectors.toList())
				);
			}
			if (mirrorZ) {
				points.addAll(points.stream()
						.map(Vector::clone)
						.map(vector -> vector.setZ(-vector.getZ()))
						.collect(Collectors.toList())
				);
			}

			registration.setPoints(points);

			// TODO: Abstract this
			ConfigurationSection optionSection = section.getConfigurationSection("options");
			if (optionSection != null) {
				ConfigurationSection speedSection = optionSection.getConfigurationSection("rotate");
				if (speedSection != null) {
					//noinspection ConstantConditions
					String name = speedSection.getString("name", "rotate").replace("\\s", "_");

					DoubleOption<CustomTrail> speedOption = new DoubleOption<>(name, 0, 0, 0,
							CustomTrail::setRotationSpeed, CustomTrail::getRotationSpeed);

					speedOption.configure(speedSection);

					double min = speedOption.getMin();
					double max = speedOption.getMax();
					double defaultSpeed = speedOption.getDefaultValue();

					if (min != max || defaultSpeed != 0) {
						registration.registerOption(speedOption);
					}
				}

				ConfigurationSection bounceSection = optionSection.getConfigurationSection("bounce");
				if (bounceSection != null) {

					ConfigurationSection bounceHeightSection = bounceSection.getConfigurationSection("height");
					if (bounceHeightSection != null) {
						//noinspection ConstantConditions
						String name = bounceHeightSection.getString("name", "height").replace("\\s", "_");

						DoubleOption<CustomTrail> bounceHeightOption = new DoubleOption<>(name, 0, 0, 0,
								CustomTrail::setBounceHeight, CustomTrail::getBounceHeight);

						bounceHeightOption.configure(bounceHeightSection);

						double minHeight = bounceHeightOption.getMin();
						double maxHeight = bounceHeightOption.getMax();
						double defaultHeight = bounceHeightOption.getDefaultValue();

						if (minHeight != maxHeight || minHeight != defaultHeight || defaultHeight != 0) {
							registration.registerOption(bounceHeightOption);

							ConfigurationSection bounceSpeedSection = bounceSection.getConfigurationSection("speed");
							if (bounceSpeedSection != null) {
								//noinspection ConstantConditions
								String speedName = bounceSpeedSection.getString("name", "rotate").replace("\\s", "_");

								DoubleOption<CustomTrail> speedOption = new DoubleOption<>(speedName, 0, 0, 0,
										CustomTrail::setBounceSpeed, CustomTrail::getBounceSpeed);

								speedOption.configure(bounceSpeedSection);

								double min = speedOption.getMin();
								double max = speedOption.getMax();
								double defaultSpeed = speedOption.getDefaultValue();

								if (min != max || defaultSpeed != 0) {
									registration.registerOption(speedOption);
								}
							}
						}
					}
				}
			}

			String trackingTypeString = section.getString("track", "absolute");
			if (trackingTypeString != null) {
				CustomTrail.TrackingType trackingType = WbsEnums.getEnumFromString(CustomTrail.TrackingType.class, trackingTypeString);

				if (trackingType != null) {
					registration.setTrackingType(trackingType);
				} else {
					logError("Invalid tracking type: " + trackingTypeString, directory + "/track");
					continue;
				}
			}

			TrailManager.registerCustomTrail(registration);
			customTrailsLoaded++;
		}

		logger.info("Loaded " + customTrailsLoaded + " custom trails!");
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
