package wbs.trails.trails;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wbs.trails.WbsTrails;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public final class TrailManager {
    private TrailManager() {}

    public static void registerNativeTrails() {
        registerTrail("aura", AuraTrail.class, AuraTrail::new);
        registerTrail("cloud", CloudTrail.class, CloudTrail::new);
        registerTrail("cube", CubeTrail.class, CubeTrail::new);
        registerTrail("electric", ElectricTrail.class, ElectricTrail::new);
        registerTrail("halo", HaloTrail.class, HaloTrail::new);
        registerTrail("helix", HelixTrail.class, HelixTrail::new);
        registerTrail("horns", HornsTrail.class, HornsTrail::new);
        registerTrail("orbiter", OrbiterTrail.class, OrbiterTrail::new);
        registerTrail("standard", StandardTrail.class, StandardTrail::new);

        WbsTrails.getInstance().logger.info("Loaded with " + registeredTrails.size() + " trails!");
    }

    private static final Map<String, RegisteredTrail<?>> registeredTrails = new HashMap<>();

    public static <T extends Trail<T>> void registerTrail(String id, Class<T> trailClass, BiFunction<RegisteredTrail<T>, Player, T> producer) {
        String formattedId = strip(id);

        RegisteredTrail<T> trail = new RegisteredTrail<>(formattedId, trailClass, producer);

        registeredTrails.put(formattedId, trail);
    }

    private static String strip(String id) {
        return id.replace(" ", "_").toLowerCase();
    }

    @Nullable
    public static RegisteredTrail<?> getRegisteredTrail(String id) {
        return registeredTrails.get(strip(id));
    }


    public static Collection<String> getTrailNames() {
        return registeredTrails.keySet();
    }

    public static Collection<RegisteredTrail<?>> getTrails() {
        return new LinkedList<>(registeredTrails.values());
    }

    public static Collection<RegisteredTrail<?>> getAllowed(Player player) {
        return getTrails().stream()
                .filter(registration -> player.hasPermission(registration.getPermission()))
                .sorted(Comparator.comparing(RegisteredTrail::getName))
                .collect(Collectors.toList());
    }
}
