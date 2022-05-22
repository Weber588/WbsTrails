package wbs.trails.trails.options;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BooleanOption<T> extends ConfigOption<T, Boolean> {
        public BooleanOption(String name, Boolean defaultValue, BiConsumer<T, Boolean> setter, Function<T, Boolean> getter) {
            super(name, defaultValue, setter, getter);
        }

        @Override
        public @NotNull List<Boolean> getAutoCompletions() {
            return Arrays.asList(true, false);
        }

        @Override
        public boolean isValid(Boolean value) {
            return value != null;
        }

        @Override
        public @Nullable Boolean fromValue(String valueString) {
            switch (valueString.toLowerCase()) {
                case "true":
                case "yes":
                case "on":
                    return true;
                case "false":
                case "no":
                case "off":
                    return false;
                default:
                    return null;
            }
        }

        @Override
        public Class<Boolean> getOptionClass() {
            return Boolean.class;
        }

        @Override
        public String getPrompt() {
            return "Please use either true or false.";
        }

        @Override
        public void configure(ConfigurationSection optionSection) {
            defaultValue = optionSection.getBoolean("default", defaultValue);
        }

        @Override
        protected Boolean valueFromConfig(ConfigurationSection section) {
            return section.getBoolean(getName(), defaultValue);
        }
}
