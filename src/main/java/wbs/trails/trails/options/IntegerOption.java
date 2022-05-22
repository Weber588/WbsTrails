package wbs.trails.trails.options;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class IntegerOption<T> extends NumericOption<T, Integer> {
    public IntegerOption(String name, int defaultValue, int min, int max, BiConsumer<T, Integer> setter, Function<T, Integer> getter) {
        super(name, defaultValue, min, max, setter, getter);
    }

    @Override
    public boolean isValid(Integer value) {
        if (value == null) return false;

        return value >= min && value <= max;
    }

    @Override
    @Nullable
    public Integer fromValue(String valueString) {
        try {
            return Integer.parseInt(valueString);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public Class<Integer> getOptionClass() {
        return Integer.class;
    }

    @Override
    public String getPrompt() {
        return "Please use an integer between " + min + " and " + max + ".";
    }

    @Override
    public void configure(ConfigurationSection optionSection) {
        defaultValue = optionSection.getInt("default", defaultValue);
        min = optionSection.getInt("min", min);
        max = optionSection.getInt("max", max);
    }

    @Override
    protected Integer valueFromConfig(ConfigurationSection section) {
        return section.getInt(getName(), defaultValue);
    }
}
