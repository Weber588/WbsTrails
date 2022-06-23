package wbs.trails.trails.presets;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.TrailsController;
import wbs.trails.WbsTrails;
import wbs.trails.trails.RegisteredTrail;
import wbs.trails.trails.Trail;
import wbs.trails.trails.TrailManager;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.WbsEnums;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PresetGroup {
    private final List<PresetTrail<?>> trails = new LinkedList<>();
    private String permission = null;
    private final String id;
    private String name;
    private String description;
    private boolean lockByDefault = true;

    @NotNull
    private Material material;

    public PresetGroup(ConfigurationSection section, String directory) {
        this.id = section.getName();
        name = section.getString("name", id);

        permission = section.getString("permission");
        description = section.getString("description");
        lockByDefault = section.getBoolean("lock-by-default", true);

        String materialString = section.getString("material", Material.BARRIER.name());
        assert materialString != null;
        Material checkMaterial = WbsEnums.getEnumFromString(Material.class, materialString);
        if (checkMaterial == null) {
            WbsTrails.getInstance().settings.logError("Invalid material: \"" + materialString + "\".", directory + "/trails");
            material = Material.BARRIER;
        } else {
            material = checkMaterial;
        }

        ConfigurationSection trailsSection = section.getConfigurationSection("trails");
        if (trailsSection == null) {
            WbsTrails.getInstance().settings.logError("Trails section missing.", directory + "/trails");
            throw new InvalidConfigurationException("Trails section missing.");
        }

        for (String presetName : trailsSection.getKeys(false)) {
            String presetDirectory = directory + "/" + presetName;

            ConfigurationSection trailSection = trailsSection.getConfigurationSection(presetName);
            if (trailSection == null) {
                WbsTrails.getInstance().settings.logError("Node must be a section: " + presetName, presetDirectory);
                continue;
            }

            trailSection.addDefault("locked", lockByDefault);

            String trailType = trailSection.getString("type");

            if (trailType == null) {
                WbsTrails.getInstance().settings.logError("Missing type in preset.", presetDirectory + "/type");
                continue;
            }

            RegisteredTrail<?> registration = TrailManager.getRegisteredTrail(trailType);

            if (registration == null) {
                WbsTrails.getInstance().settings.logError("Invalid type: " + trailType, presetDirectory + "/type");
                continue;
            }

            try {
                PresetTrail<?> trail = new PresetTrail<>(registration, trailSection);
                trails.add(trail);
            } catch (InvalidConfigurationException e) {
                WbsTrails.getInstance().settings.logError(e.getMessage(), presetDirectory);
            }
        }

        if (trails.isEmpty()) {
            WbsTrails.getInstance().settings.logError("No valid trails.", directory + "/trails");
            throw new InvalidConfigurationException("No valid trails.");
        }
    }

    public PresetGroup(PresetTrail<?> trail, @NotNull String name) {
        this(trail, name, name);
    }

    public PresetGroup(PresetTrail<?> trail, @NotNull String id, @NotNull String name) {
        this.id = PresetManager.formatId(id);
        this.name = name;
        material = trail.getRegistration().getMaterial();

        trails.add(trail);
    }

    public PresetGroup(Collection<PresetTrail<?>> trails, @NotNull String id, @NotNull String name) {
        this.id = PresetManager.formatId(id);
        this.name = name;

        this.trails.addAll(trails);
        material = this.trails.get(0).getRegistration().getMaterial();
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getId() {
        return id;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(getPermission());
    }

    @NotNull
    public String getPermission() {
        return permission == null ? "" : permission;
    }


    public void writeToConfig(ConfigurationSection section) {
        section.set("name", name);
        section.set("permission", "wbstrails.preset." + id);
        section.set("description", description);
        section.set("material", material.name());
        section.set("lock-by-default", lockByDefault);

        int i = 0;
        for (PresetTrail<?> trail : trails) {
            trail.writeToConfig(section, "trails." + i);
            i++;
        }
    }

    @NotNull
    public Material getMaterial() {
        return material;
    }

    public List<PresetTrail<?>> getTrails() {
        return new LinkedList<>(trails);
    }

    public void apply(Player player) {
        TrailsController controller = TrailsController.getInstance();

        controller.clearTrails(player);

        for (PresetTrail<?> preset : trails) {
            Trail<?> trail = preset.getTrail(player);
            if (controller.addTrail(player, trail)) {
                trail.enable();
            } else {
                controller.clearTrails(player);
                WbsTrails.getInstance().logger.severe("Failed to add trail during preset initialization. Please report this issue.");
                return;
            }
        }
    }

    public void setMaterial(@NotNull Material material) {
        this.material = material;
    }
}
