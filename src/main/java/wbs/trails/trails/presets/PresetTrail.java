package wbs.trails.trails.presets;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.trails.RegisteredTrail;
import wbs.trails.trails.Trail;
import wbs.trails.trails.data.DataManager;
import wbs.trails.trails.data.DataProducer;
import wbs.trails.trails.options.ConfigOption;
import wbs.trails.trails.options.OptionPair;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.WbsEnums;

import java.util.LinkedList;
import java.util.List;

public class PresetTrail<T extends Trail<T>> {
    @NotNull
    private final RegisteredTrail<T> registration;
    private final List<OptionPair<T, ?>> filledOptions = new LinkedList<>();

    @NotNull
    private final String id;

    private String permission = null;
    @NotNull
    private String name;

    private String description;

    @NotNull
    private final Particle particle;
    private DataProducer<?, ?> data;

    public PresetTrail(@NotNull RegisteredTrail<T> registration, ConfigurationSection section) throws InvalidConfigurationException {
        this.registration = registration;
        this.id = section.getName();

        permission = section.getString("permission");
        //noinspection ConstantConditions
        name = section.getString("name", id);

        String particleName = section.getString("particle");
        if (particleName == null) {
            throw new InvalidConfigurationException("Particle is a required field.");
        }

        Particle checkParticle = WbsEnums.getEnumFromString(Particle.class, particleName);
        if (checkParticle == null) {
            throw new InvalidConfigurationException("Invalid particle: " + particleName);
        }

        particle = checkParticle;

        String optionClassName = section.getString("data-type");
        if (optionClassName != null) {
            Class<?> dataClass = DataManager.classFromString(optionClassName);

            if (dataClass == null) {
                throw new InvalidConfigurationException("Invalid/unregistered particle data class: " + optionClassName);
            }

            data = DataManager.deserialize(dataClass, section, "data");

            if (data == null) {
                throw new InvalidConfigurationException("Invalid dataClass: " + dataClass.getCanonicalName());
            }
        }

        description = section.getString("description");

        for (ConfigOption<T, ?> option : registration.getOptions()) {
            filledOptions.add(option.fromConfig(section));
        }
    }

    public PresetTrail(T trail, @NotNull String id) {
        this.id = id;
        name = id;
        particle = trail.getParticle();
        data = trail.getData();
        registration = trail.getRegistration();
        for (ConfigOption<T, ?> option : registration.getOptions()) {
            filledOptions.add(option.fromT(trail));
        }
    }

    @NotNull
    public T getTrail(Player player) {
        T trail = registration.buildTrail(player);

        for (OptionPair<T, ?> pair : filledOptions) {
            pair.apply(trail);
        }

        trail.setParticle(particle);
        trail.setData(data);

        return trail;
    }

    public void writeToConfig(ConfigurationSection section) {
        section.set("type", registration.getName());
        section.set("particle", particle.name());
        section.set("permission", "wbstrails.preset." + id);
        section.set("description", description);

        if (data != null) {
            section.set("data-type", data.getDataClass().getCanonicalName());
            data.writeToConfig(section, "data");
        }

        for (OptionPair<T, ?> pair : filledOptions) {
            pair.writeToConfig(section);
        }
    }

    @NotNull
    public String getPermission() {
        return permission == null ? "" : permission;
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

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(getPermission()) && player.hasPermission(registration.getPermission());
    }
}
