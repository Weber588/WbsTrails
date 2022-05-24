package wbs.trails.menus.build;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.WbsTrails;
import wbs.trails.trails.RegisteredTrail;
import wbs.trails.trails.data.DataManager;
import wbs.trails.trails.data.DataProducer;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.PagedMenu;
import wbs.utils.util.menus.WbsMenu;
import wbs.utils.util.particles.NormalParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;
import wbs.utils.util.plugin.WbsPlugin;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ChooseParticleMenu extends PagedMenu<Particle> {

    private final Player player;
    @NotNull
    private final WbsTrails plugin;
    private final RegisteredTrail<?> registration;

    public ChooseParticleMenu(@NotNull WbsTrails plugin, RegisteredTrail<?> registration, Player player) {
        this(plugin, registration, player, 0);
    }
    public ChooseParticleMenu(@NotNull WbsTrails plugin, RegisteredTrail<?> registration, Player player, int page) {
        super(plugin,
                plugin.settings.getAllowedParticlesFor(player).stream()
                        .sorted(Comparator.comparing(Particle::name))
                        .collect(Collectors.toList()),
                "&4&lChoose a particle!",
                "particle:" + player.getUniqueId(),
                1,
                4,
                1,
                7,
                page);

        this.player = player;
        this.plugin = plugin;
        this.registration = registration;

        setUnregisterOnClose(true);
    }

    @Override
    protected MenuSlot getSlot(Particle particle) {
        List<String> lore = new LinkedList<>();
        lore.add("&7Click to choose!");
        lore.add("&7(Shift+click to preview!)");

        String name = "&b" + WbsEnums.toPrettyString(particle);

        // Use super plugin since ChooseParticleMenu's constructor hasn't been called yet
        MenuSlot slot = new MenuSlot(super.plugin,
                Material.RED_DYE,
                super.plugin.dynamicColourise(name),
                true,
                super.plugin.colouriseAll(lore));

        slot.setClickAction(event -> {
            if (event.isShiftClick()) {
                WbsParticleEffect effect = new NormalParticleEffect().setX(1).setY(2).setZ(1).setAmount(15);

                DataProducer<?, ?> producer = DataManager.getProducer(particle.getDataType());

                Object options = null;
                if (producer != null) {
                    options = producer.produce();
                }

                if (options != null) {
                    effect.setOptions(options);
                }

                effect.play(particle, WbsEntityUtil.getMiddleLocation(player), player);
                return;
            }

            // TODO: Go to DataOptionsMenu if particle has data
            new TrailOptionsMenu<>(plugin, registration, player).showTo(player);
        });

        return slot;
    }

    @Override
    protected PagedMenu<Particle> getPage(int page) {
        return new ChooseParticleMenu(plugin, registration, player, page);
    }
}
