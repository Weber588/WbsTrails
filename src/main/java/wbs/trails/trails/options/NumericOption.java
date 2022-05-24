package wbs.trails.trails.options;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class NumericOption<T, V extends Number> extends ConfigOption<T, V> {
    protected V min;
    protected V max;

    public NumericOption(String name, @NotNull V defaultValue, V min, V max, BiConsumer<T, V> setter, Function<T, V> getter) {
        super(name, defaultValue, setter, getter);
        this.min = min;
        this.max = max;
    }

    @Override
    public @NotNull List<V> getAutoCompletions() {
        return Arrays.asList(defaultValue, min, max);
    }

    public V getMin() {
        return min;
    }

    public V getMax() {
        return max;
    }
}