package wbs.trails.trails;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.trails.trails.options.TrailOptionProvider;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.providers.VectorProvider;

import java.util.LinkedList;
import java.util.List;

public class CustomRegisteredTrail extends RegisteredTrail<CustomTrail> {

    @Nullable
    private static CustomTrail registering;
    public static void registerToNewTrail(TrailOptionProvider provider) {
        if (registering != null) {
            registering.registerProvider(provider);
        }
    }

    @NotNull
    private CustomTrail.TrackingType trackingType = CustomTrail.TrackingType.ABSOLUTE;
    private final List<Vector> points = new LinkedList<>();
    private NumProvider rotation;
    private VectorProvider offset;

    public CustomRegisteredTrail(@NotNull String name) throws InvalidConfigurationException {
        super(name, CustomTrail.class, CustomTrail::new);

        description = "A custom trail.";
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public void setPoints(List<Vector> points) {
        this.points.clear();
        this.points.addAll(points);
    }

    public void setTrackingType(@NotNull CustomTrail.TrackingType trackingType) {
        this.trackingType = trackingType;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setRotation(NumProvider rotation) {
        this.rotation = rotation;
    }

    public void setOffset(VectorProvider offset) {
        this.offset = offset;
    }

    @Override
    public synchronized CustomTrail buildTrail(Player player) {
        CustomTrail customTrail = super.buildTrail(player);

        registering = customTrail;

        customTrail.setTrackingType(trackingType);
        customTrail.setPoints(points);
        customTrail.setDescription(description);

        if (rotation != null) {
            customTrail.setRotation(new NumProvider(rotation));
        } else {
            customTrail.setRotation(new NumProvider(0));
        }

        if (offset != null) {
            customTrail.setOffset(new VectorProvider(offset));
        } else {
            customTrail.setOffset(new VectorProvider(0, 0, 0));
        }

        registering = null;

        getOptions().forEach(option -> option.applyDefault(customTrail));

        return customTrail;
    }
}
