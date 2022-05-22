package wbs.trails.trails.data;

import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class DataManager {

    private DataManager() {}

    private static final Map<Class<?>, Supplier<?>> producers = new HashMap<>();
    private static final Map<Class<?>, BiFunction<ConfigurationSection, String, ?>> deserializers = new HashMap<>();

    private static <T, P extends DataProducer<T, P>> void registerProducer(Class<T> dataClass,
                                                                           Supplier<?> constructor,
                                                                           BiFunction<ConfigurationSection, String, P> deserialize) {
        producers.put(dataClass, constructor);
        deserializers.put(dataClass, deserialize);
    }

    static {
        registerProducer(Particle.DustOptions.class,
                DustDataProducer::new,
                DustDataProducer::deserialize);
        registerProducer(ItemStack.class,
                ItemStackProducer::new,
                ItemStackProducer::deserialize);
        registerProducer(BlockData.class,
                BlockDataProducer::new,
                BlockDataProducer::deserialize);
    }

    /**
     * Build particle data from the mapped producer.
     * @param dataClass The data class to use
     * @return null if the data class was not mapped. If mapped, the built particle data,
     * which may contain the data or an error string.
     */
    @Nullable
    public static <T, P extends DataProducer<T, P>> P getProducer(Class<T> dataClass) {
        Supplier<?> untypedProducer = producers.get(dataClass);
        if (untypedProducer == null) return null;

        @SuppressWarnings("unchecked")
        Supplier<P> producer = (Supplier<P>) untypedProducer;

        return producer.get();
    }

    /**
     * Get tab completions for the given args, or null if the data won't
     * use any more args after start.
     * @param dataClass The data class to use
     * @param args The args to build from
     * @return The suggestions, or null if out of range.
     */
    @Nullable
    public static <T, P extends DataProducer<T, P>> List<String> handleTab(Class<?> dataClass, String[] args) {
        Supplier<?> untypedProducer = producers.get(dataClass);
        if (untypedProducer == null) return null;

        @SuppressWarnings("unchecked")
        Supplier<P> producer = (Supplier<P>) untypedProducer;

        return producer.get().handleTab(args);
    }

    public static <T, P extends DataProducer<T, P>> P deserialize(Class<T> dataClass, ConfigurationSection section, String path) {
        BiFunction<ConfigurationSection, String, ?> deserializer = deserializers.get(dataClass);
        if (deserializer == null) return null;

        //noinspection unchecked
        return ((BiFunction<ConfigurationSection, String, P>) deserializer).apply(section, path);
    }

    public static Class<?> classFromString(String canonicalName) {
        for (Class<?> clazz : producers.keySet()) {
            if (clazz.getCanonicalName().equalsIgnoreCase(canonicalName)) {
                return clazz;
            }
        }

        return null;
    }
}
