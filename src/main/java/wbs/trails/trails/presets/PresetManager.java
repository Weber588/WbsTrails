package wbs.trails.trails.presets;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wbs.trails.WbsTrails;
import wbs.utils.exceptions.InvalidConfigurationException;

import java.util.*;
import java.util.stream.Collectors;

public final class PresetManager {
    private PresetManager() {}

    private static final Map<String, PresetGroup> presets = new HashMap<>();

    public static void setPreset(String id, PresetGroup preset) {
        presets.put(id, preset);

        WbsTrails.getInstance().runAsync(() -> WbsTrails.getInstance().settings.savePresets());
    }

    @Nullable
    public static PresetGroup getPreset(String id) {
        return presets.get(id);
    }

    public static ConfigurationSection writePresets(ConfigurationSection section) {
        for (String presetId : presets.keySet()) {
            PresetGroup preset = presets.get(presetId);

            ConfigurationSection trailSection = section.createSection(presetId);

            preset.writeToConfig(trailSection);
        }

        return section;
    }

    public static void loadPresets(ConfigurationSection section, String directory) {
        presets.clear();
        for (String presetName : section.getKeys(false)) {
            ConfigurationSection trailSection = section.getConfigurationSection(presetName);

            if (trailSection == null) {
                WbsTrails.getInstance().settings.logError("Preset must be a section: " + presetName, directory + "/" + presetName);
                continue;
            }

            PresetGroup preset;
            try {
                preset = new PresetGroup(trailSection, directory + "/" + presetName);
            } catch (InvalidConfigurationException e) {
                continue;
            }

            presets.put(presetName, preset);
        }
    }

    public static Collection<String> getPresetNames() {
        return new LinkedList<>(presets.keySet());
    }

    public static List<PresetGroup> getPresets() {
        return new LinkedList<>(presets.values());
    }

    public static Collection<PresetGroup> getAllowed(Player player) {
        return getPresets().stream()
                .filter(presetGroup -> presetGroup.hasPermission(player))
                .sorted(Comparator.comparing(PresetGroup::getName))
                .collect(Collectors.toList());
    }

    public static String formatId(String name) {
        return name.toLowerCase().replace(" ", "_");
    }
}
