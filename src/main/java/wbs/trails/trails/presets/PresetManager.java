package wbs.trails.trails.presets;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import wbs.trails.WbsTrails;
import wbs.trails.trails.RegisteredTrail;
import wbs.trails.trails.TrailManager;
import wbs.utils.exceptions.InvalidConfigurationException;

import java.util.*;

public final class PresetManager {
    private PresetManager() {}

    private static final Map<String, PresetTrail<?>> presets = new HashMap<>();

    public static void setPreset(String id, PresetTrail<?> preset) {
        presets.put(id, preset);
    }

    @Nullable
    public static PresetTrail<?> getPreset(String id) {
        return presets.get(id);
    }

    public static ConfigurationSection savePresets(ConfigurationSection section) {
        for (String presetId : presets.keySet()) {
            PresetTrail<?> preset = presets.get(presetId);

            ConfigurationSection trailSection = section.createSection(presetId);

            preset.writeToConfig(trailSection);
        }

        return section;
    }

    public static void loadPresets(ConfigurationSection section, String directory) {
        for (String presetName : section.getKeys(false)) {
            ConfigurationSection trailSection = section.getConfigurationSection(presetName);

            if (trailSection == null) {
                WbsTrails.getInstance().settings.logError("Preset must be a section: " + presetName, directory + "/" + presetName);
                continue;
            }

            String trailType = trailSection.getString("type");

            if (trailType == null) {
                WbsTrails.getInstance().settings.logError("Missing type in preset.", directory + "/" + presetName + "/type");
                continue;
            }

            RegisteredTrail<?> registration = TrailManager.getRegisteredTrail(trailType);

            if (registration == null) {
                WbsTrails.getInstance().settings.logError("Invalid type: " + trailType, directory + "/" + presetName + "/type");
                continue;
            }

            PresetTrail<?> preset;
            try {
                preset = new PresetTrail<>(registration, trailSection);
            } catch (InvalidConfigurationException e) {
                WbsTrails.getInstance().settings.logError(e.getMessage(), directory + "/" + presetName);
                continue;
            }

            presets.put(presetName, preset);
        }
    }

    public static Collection<String> getPresetNames() {
        return new LinkedList<>(presets.keySet());
    }

    public static List<PresetTrail<?>> getPresets() {
        return new LinkedList<>(presets.values());
    }
}
