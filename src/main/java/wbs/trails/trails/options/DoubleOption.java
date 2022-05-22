package wbs.trails.trails.options;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DoubleOption<T> extends NumericOption<T, Double> {
    public DoubleOption(String name, double defaultValue, double min, double max, BiConsumer<T, Double> setter, Function<T, Double> getter) {
        super(name, defaultValue, min, max, setter, getter);
    }

    @Override
    public boolean isValid(Double value) {
        if (value == null) return false;

        return value >= min && value <= max;
    }

    @Override
    @Nullable
    public Double fromValue(String valueString) {
        try {
            return Double.parseDouble(valueString);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public Class<Double> getOptionClass() {
        return null;
    }

    @Override
    public String getPrompt() {
        return "Please use a decimal between " + min + " and " + max + ".";
    }

    @Override
    public void configure(ConfigurationSection optionSection) {
        defaultValue = optionSection.getDouble("default", defaultValue);
        min = optionSection.getDouble("min", min);
        max = optionSection.getDouble("max", max);
    }

    @Override
    protected Double valueFromConfig(ConfigurationSection section) {
        return section.getDouble(getName(), defaultValue);
    }
}
