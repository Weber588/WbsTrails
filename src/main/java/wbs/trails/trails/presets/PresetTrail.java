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
    private final Particle particle;
    private DataProducer<?, ?> data;

    private boolean lockByDefault = true;
    private boolean locked = lockByDefault;

    public PresetTrail(@NotNull RegisteredTrail<T> registration, ConfigurationSection section) throws InvalidConfigurationException {
        this.registration = registration;

        ConfigurationSection defaultConfig = section.getDefaultSection();
        if (defaultConfig != null) {
            lockByDefault = defaultConfig.getBoolean("locked", lockByDefault);
        }
        locked = section.getBoolean("locked");

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

        for (ConfigOption<T, ?> option : registration.getOptions()) {
            filledOptions.add(option.fromConfig(section));
        }
    }

    public PresetTrail(T trail) {
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

        trail.setLocked(locked);

        for (OptionPair<T, ?> pair : filledOptions) {
            pair.apply(trail);
        }

        trail.setParticle(particle);
        trail.setData(data);

        return trail;
    }

    public void writeToConfig(ConfigurationSection section, String path) {
        section.set(path + ".type", registration.getName());
        section.set(path + ".particle", particle.name());
        if (lockByDefault != locked) {
            section.set(path + ".locked", locked);
        }

        if (data != null) {
            section.set(path + ".data-type", data.getDataClass().getCanonicalName());
            data.writeToConfig(section, path + ".data");
        }

        for (OptionPair<T, ?> pair : filledOptions) {
            pair.writeToConfig(section, path + ".options");
        }
    }

    @NotNull
    public RegisteredTrail<T> getRegistration() {
        return registration;
    }

    public DataProducer<?, ?> getData() {
        return data;
    }

    public List<OptionPair<T, ?>> getOptions() {
        return new LinkedList<>(filledOptions);
    }

    @NotNull
    public Particle getParticle() {
        return particle;
    }
}
