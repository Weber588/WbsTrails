package wbs.trails.trails;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import wbs.utils.exceptions.InvalidConfigurationException;

import java.util.LinkedList;
import java.util.List;

public class CustomRegisteredTrail extends RegisteredTrail<CustomTrail> {

    @NotNull
    private CustomTrail.TrackingType trackingType = CustomTrail.TrackingType.ABSOLUTE;
    private final List<Vector> points = new LinkedList<>();

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

    @Override
    public CustomTrail buildTrail(Player player) {
        CustomTrail customTrail = super.buildTrail(player);

        customTrail.setTrackingType(trackingType);
        customTrail.setPoints(points);
        customTrail.setDescription(description);

        return customTrail;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
