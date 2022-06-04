package wbs.trails.trails.options;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.utils.util.menus.PageSlot;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class ConfigOption<T, V> {
    private final String name;
    @NotNull
    protected V defaultValue;
    private final BiConsumer<T, V> setter;
    private final Function<T, V> getter;

    public ConfigOption(String name, @NotNull V defaultValue, BiConsumer<T, V> setter, Function<T, V> getter) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.setter = setter;
        this.getter = getter;
    }

    public String getName() {
        return name;
    }

    @NotNull
    public abstract List<V> getAutoCompletions();

    /**
     * Check if a given value is a valid value for this option
     * @param value The value to check
     * @return True if the value is valid for this option
     */
    public abstract boolean isValid(V value);


    /**
     * Parse a string into this type, typically via String.valueOf
     * @param valueString The string value to parse
     * @return The value of this type, or null if it failed to parse
     */
    @Nullable
    public abstract V fromValue(String valueString);

    public abstract Class<V> getOptionClass();

    public abstract String getPrompt();

    public void apply(T applyTo, V value) {
        setter.accept(applyTo, value);
    }

    public void applyDefault(T applyTo) {
        setter.accept(applyTo, defaultValue);
    }

    public final OptionPair<T, V> fromT(T from) {
        V value = getter.apply(from);
        return new OptionPair<>(this, value);
    }

    public final OptionPair<T, V> pairFromValue(String valueString) {
        V value = fromValue(valueString);

        if (value != null) {
            if (isValid(value)) {
                return new OptionPair<>(this, value);
            }
        }

        return null;
    }

    public abstract void configure(ConfigurationSection optionSection);

    public final OptionPair<T, V> fromConfig(ConfigurationSection section) {
        V value = valueFromConfig(section);
        return new OptionPair<>(this, value);
    }

    protected abstract V valueFromConfig(ConfigurationSection section);

    @NotNull
    public V getDefaultValue() {
        return defaultValue;
    }

    public abstract PageSlot<ConfigOption<T, V>> newSlot();

    public boolean isEditable() {
        return true;
    }
}
