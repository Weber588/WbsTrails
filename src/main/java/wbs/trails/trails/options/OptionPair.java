package wbs.trails.trails.options;

import org.bukkit.configuration.ConfigurationSection;

public class OptionPair<T, V> {
    private final ConfigOption<T, V> option;
    private final V value;

    public OptionPair(ConfigOption<T, V> option, V value) {
        this.option = option;
        this.value = value;
    }

    public OptionPair(ConfigOption<T, V> option, String valueString) {
        V value = option.fromValue(valueString);

        if (value != null) {
            if (option.isValid(value)) {
                this.value = value;
            }
        }

        throw new IllegalArgumentException(option.getPrompt());
    }

    public void apply(T applyTo) {
        option.apply(applyTo, value);
    }

    public void writeToConfig(ConfigurationSection section, String path) {
        section.set(path + "." + option.getName(), value);
    }

    public V getValue() {
        return value;
    }

    public ConfigOption<T, V> getOption() {
        return option;
    }
}