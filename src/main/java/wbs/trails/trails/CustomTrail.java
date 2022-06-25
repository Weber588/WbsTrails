package wbs.trails.trails;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import wbs.trails.trails.options.TrailOptionProvider;
import wbs.utils.util.WbsMath;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.entities.WbsPlayerUtil;
import wbs.utils.util.particles.CustomParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.providers.VectorProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomTrail extends Trail<CustomTrail> {

    private List<Vector> points;
    private String description;
    private TrackingType trackingType = TrackingType.ABSOLUTE;

    private final Multimap<String, TrailOptionProvider> registeredProviders = HashMultimap.create();
    private final Map<String, Double> currentValues = new HashMap<>();

    private NumProvider rotation;
    private VectorProvider offset;

    public CustomTrail(RegisteredTrail<CustomTrail> registration, Player player) {
        super(registration, player);
    }

    private final CustomParticleEffect effect = new CustomParticleEffect() {
        @Override
        public CustomParticleEffect play(Particle particle, Location loc) {
            refreshProviders();

            World world = loc.getWorld();
            assert world != null;
            ArrayList<Location> locations = getLocations(loc);

            if (options == null) {
                for (Location point : locations) {
                    world.spawnParticle(particle, point, 1, 0, 0, 0, 0, null, true);
                }
            } else {
                for (Location point : locations) {
                    world.spawnParticle(particle, point, 1, 0, 0, 0, 0, particle.getDataType().cast(options), true);
                }
            }
            return this;
        }

        @Override
        public WbsParticleEffect play(Particle particle, Location location, Player player) {
            refreshProviders();

            ArrayList<Location> locations = getLocations(location);

            if (options == null) {
                for (Location point : locations) {
                    player.spawnParticle(particle, point, 1, 0, 0, 0, 0, null);
                }
            } else {
                for (Location point : locations) {
                    player.spawnParticle(particle, point, 1, 0, 0, 0, 0, particle.getDataType().cast(options));
                }
            }
            return this;
        }
    };

    private final static Vector upVector = new Vector(0, 1, 0);

    @Override
    public void tick() {
        for (int i = 0; i < settings.getRefreshRate(); i++) {
            rotation.refresh();
            offset.refresh();
        }

        List<Vector> newPoints;

        Vector localUp = upVector;
        Location playLocation = player.getLocation();

        Vector offset = null;
        if (this.offset != null) {
            offset = this.offset.val();
        }

        switch (trackingType) {
            case ABSOLUTE:
                newPoints = points.stream()
                        .map(Vector::clone)
                        .collect(Collectors.toList());
                break;
            case BODY:
                playLocation = WbsEntityUtil.getMiddleLocation(player);
                newPoints = WbsMath.rotateVectors(points,
                        upVector,
                        0 - player.getLocation().getYaw());

                if (offset != null) {
                    offset = WbsMath.rotateVector(offset,
                            upVector,
                            0 - player.getLocation().getYaw());
                }
                break;
            case HEAD:
                localUp = WbsEntityUtil.getLocalUp(player);
                playLocation = WbsPlayerUtil.getNeckPosition(player).add(localUp.clone().normalize().multiply(0.2));

                newPoints = WbsMath.rotateFrom(points,
                        localUp,
                        upVector);

                newPoints = WbsMath.rotateVectors(newPoints,
                        localUp,
                        0 - player.getLocation().getYaw());

                if (offset != null) {
                    offset = WbsMath.rotateFrom(offset,
                            localUp,
                            upVector);

                    offset = WbsMath.rotateVector(offset,
                            localUp,
                            0 - player.getLocation().getYaw());
                }
                break;
            default:
                settings.logError("Tracking type not recognized: " + trackingType + ". Please report this error.",
                        "custom_trails.yml/" + getRegistration().getName() + "/track");
                disable();
                sendMessage("&wAn internal error occurred. Please check console, or report this error.", player);
                return;
        }

        if (rotation != null) {
            double rotation = this.rotation.val();

            newPoints = WbsMath.rotateVectors(newPoints, localUp, rotation);
        }

        if (offset != null) {
            Vector finalOffset = offset;
            newPoints.forEach(point -> point.add(finalOffset));
        }

        effect.setPoints(newPoints);

        // buildAndPlay to refresh providers if used
        effect.buildAndPlay(particle, playLocation);
    }

    @Override
    public CustomParticleEffect getEffect() {
        return effect;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    protected CustomTrail getThis() {
        return this;
    }

    public void setPoints(List<Vector> points) {
        this.points = points;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTrackingType(TrackingType trackingType) {
        this.trackingType = trackingType;
    }

    public void setRotation(NumProvider rotation) {
        this.rotation = rotation;
    }

    public void setOffset(VectorProvider offset) {
        this.offset = offset;
    }

    public void registerProvider(TrailOptionProvider provider) {
        registeredProviders.put(provider.getName(), provider);
    }

    public void setProviderVal(String name, double val) {
        currentValues.put(name, val);
        for (TrailOptionProvider provider : registeredProviders.get(name)) {
            provider.setValue(val);
        }
    }

    public double getProviderVal(String name) {
        return currentValues.get(name);
    }

    public enum TrackingType {
        ABSOLUTE,
        BODY,
        HEAD
    }
}
