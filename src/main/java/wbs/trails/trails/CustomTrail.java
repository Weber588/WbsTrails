package wbs.trails.trails;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.VersionUtil;
import wbs.utils.util.WbsMath;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.entities.WbsPlayerUtil;
import wbs.utils.util.particles.CustomParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomTrail extends Trail<CustomTrail> {

    private List<Vector> points;
    private String description;
    private TrackingType trackingType = TrackingType.ABSOLUTE;

    private double rotationSpeed = 0;
    private double bounceSpeed = 0;
    private double bounceHeight = 0;

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

    private double age = 0;

    @Override
    public void tick() {
        List<Vector> newPoints;

        Vector localUp = upVector;
        Location playLocation = player.getLocation();

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
                break;
            default:
                settings.logError("Tracking type not recognized: " + trackingType + ". Please report this error.",
                        "custom_trails.yml/" + getRegistration().getName() + "/track");
                disable();
                sendMessage("&wAn internal error occurred. Please check console, or report this error.", player);
                return;
        }

        age += settings.getRefreshRate();

        if (rotationSpeed != 0) {
            double rotation = age * rotationSpeed;

            newPoints = WbsMath.rotateVectors(newPoints, localUp, rotation);
        }

        if (bounceHeight != 0 && bounceSpeed != 0) {
            double height = Math.sin(age * bounceSpeed) * bounceHeight;
            Vector offset = localUp.clone().normalize().multiply(height);
            newPoints = newPoints.stream()
                    .map(point -> point.add(offset))
                    .collect(Collectors.toList());
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

    public double getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(double rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public void setTrackingType(@NotNull TrackingType trackingType) {
        this.trackingType = trackingType;
    }

    public double getBounceSpeed() {
        return bounceSpeed;
    }

    public void setBounceSpeed(double bounceSpeed) {
        this.bounceSpeed = bounceSpeed;
    }

    public double getBounceHeight() {
        return bounceHeight;
    }

    public void setBounceHeight(double bounceHeight) {
        this.bounceHeight = bounceHeight;
    }

    public enum TrackingType {
        ABSOLUTE,
        BODY,
        HEAD
    }
}
