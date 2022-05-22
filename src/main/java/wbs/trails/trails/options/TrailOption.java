package wbs.trails.trails.options;

import org.jetbrains.annotations.NotNull;
import wbs.trails.trails.Trail;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * An option that can be used in the configuration of a {@link Trail}
 * @param <T> The type of the trail
 * @param <V> The type of this option to be used by a trail
 */
public abstract class TrailOption<T extends Trail<T>, V> extends ConfigOption<T, V> {
    public TrailOption(String name, @NotNull V defaultValue, BiConsumer<T, V> setter, Function<T, V> getter) {
        super(name, defaultValue, setter, getter);
    }
}
