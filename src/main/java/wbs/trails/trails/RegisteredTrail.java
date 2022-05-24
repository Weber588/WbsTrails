package wbs.trails.trails;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.trails.options.ConfigOption;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiFunction;

public class RegisteredTrail<T extends Trail<T>> {
    private static final String REGISTER_OPTIONS_METHOD = "registerOptions";

    @NotNull
    private final String name;
    @NotNull
    private final Class<T> trailClass;
    @NotNull
    private final BiFunction<RegisteredTrail<T>, Player, T> producer;

    private String description;

    private final Map<String, ConfigOption<T, ?>> options = new HashMap<>();

    public RegisteredTrail(@NotNull String name, @NotNull Class<T> trailClass, @NotNull BiFunction<RegisteredTrail<T>, Player, T> producer) throws InvalidTrailClassException {
        this.name = name;
        this.trailClass = trailClass;
        this.producer = producer;

        try {
            Method registerOption = trailClass.getDeclaredMethod(REGISTER_OPTIONS_METHOD, RegisteredTrail.class);

            registerOption.invoke(null, this);
        } catch (NoSuchMethodException ignored) {
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new InvalidTrailClassException(trailClass, "An exception occurred while calling " + REGISTER_OPTIONS_METHOD + ".");
        } catch (IllegalAccessException e) {
            throw new InvalidTrailClassException(trailClass, REGISTER_OPTIONS_METHOD + " must be public and static.");
        }
    }

    public void registerOption(ConfigOption<T, ?> option) {
        options.put(option.getName().toLowerCase(), option);
    }

    public T buildTrail(Player player) {
        return producer.apply(this, player);
    }

    @NotNull
    public Class<T> getTrailClass() {
        return trailClass;
    }

    public ConfigOption<T, ?> getOption(String optionName) {
        return options.get(optionName.toLowerCase());
    }

    public Collection<String> getOptionNames() {
        return options.keySet();
    }

    public String getDescription() {
        return description;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void configure(ConfigurationSection section) {
        for (ConfigOption<T, ?> option : options.values()) {
            ConfigurationSection optionSection = section.getConfigurationSection(option.getName());
            if (optionSection != null) {
                option.configure(optionSection);
            }
        }

        description = section.getString("description");
    }

    public Collection<ConfigOption<T, ?>> getOptions() {
        return new LinkedList<>(options.values());
    }

    public String getPermission() {
        return "wbstrails.type." + getName().toLowerCase();
    }
}
