package wbs.trails.trails;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.trails.options.ConfigOption;
import wbs.utils.util.WbsEnums;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class RegisteredTrail<T extends Trail<T>> {
    private static final String REGISTER_OPTIONS_METHOD = "registerOptions";

    @NotNull
    private final String name;
    @NotNull
    private final Class<T> trailClass;
    @NotNull
    private final BiFunction<RegisteredTrail<T>, Player, T> producer;

    @NotNull
    protected String description = "";
    @NotNull
    protected Material material;

    private final Map<String, ConfigOption<T, ?>> options = new HashMap<>();

    public RegisteredTrail(@NotNull String name, @NotNull Class<T> trailClass, @NotNull BiFunction<RegisteredTrail<T>, Player, T> producer) throws InvalidTrailClassException {
        this.name = name;
        this.trailClass = trailClass;
        this.producer = producer;
        material = Material.OAK_SIGN;

        // TODO: Move from reflection to lambda
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
        T trail = producer.apply(this, player);

        for (ConfigOption<T, ?> option : options.values()) {
            option.applyDefault(trail);
        }

        return trail;
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

    public Collection<ConfigOption<T, ?>> getOptions() {
        return new LinkedList<>(options.values());
    }

    public Collection<String> getUsableOptionNames(Player player) {
        return options.values().stream()
                .filter(ConfigOption::isEditable)
                .filter(option -> player.hasPermission(getPermission(option)))
                .map(ConfigOption::getName)
                .collect(Collectors.toList());
    }

    public Collection<ConfigOption<T, ?>> getUsableOptions(Player player) {
        return options.values().stream()
                .filter(ConfigOption::isEditable)
                .filter(option -> player.hasPermission(getPermission(option)))
                .collect(Collectors.toList());
    }

    @NotNull
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

        String checkDescription = section.getString("description");
        if (checkDescription != null) {
            description = checkDescription;
        }
        material = WbsEnums.materialFromString(section.getString("material"), material);
    }

    public String getPermission() {
        return "wbstrails.type." + getName().toLowerCase();
    }

    public String getPermission(ConfigOption<T, ?> option) {
        return getPermission() + "." + option.getName();
    }

    @NotNull
    public Material getMaterial() {
        return material;
    }
}
