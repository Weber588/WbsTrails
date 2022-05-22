package wbs.trails.trails.data;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.trails.trails.options.ConfigOption;

import java.util.Collection;
import java.util.List;

public abstract class DataProducer<T, P extends DataProducer<T, P>> {

    public DataProducer() {}

    public DataProducer(String[] args) throws IllegalArgumentException {
        configure(args);
    }

    /**
     * Create a dust data of type {@link T}, wrapped in BuiltParticleData if valid,
     * or setting an error if invalid.
     * @return The BuiltParticleData, which either contains the data, or an error string.
     */
    @NotNull
    public abstract T produce();

    /**
     * Get tab completions for the given args, or null if the data won't
     * use any more args after start.
     * @return The suggestions, or null if out of range.
     */
    @Nullable
    public abstract List<String> handleTab(String[] args);

    public abstract String getUsage();

    public abstract int configure(String[] args) throws IllegalArgumentException;

    public abstract void writeToConfig(ConfigurationSection section, String path);

    public abstract Collection<ConfigOption<P, ?>> getDataOptions();

    public abstract Class<T> getDataClass();
}
