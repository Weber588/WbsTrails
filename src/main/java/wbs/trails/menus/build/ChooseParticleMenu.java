package wbs.trails.menus.build;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wbs.trails.WbsTrails;
import wbs.trails.menus.TrailMenuUtils;
import wbs.trails.trails.Trail;
import wbs.trails.trails.data.DataManager;
import wbs.trails.trails.data.DataProducer;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.PageSlot;
import wbs.utils.util.menus.PagedMenu;
import wbs.utils.util.particles.NormalParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ChooseParticleMenu<T extends Trail<T>> extends PagedMenu<Particle> implements MenuPage {

    private final MenuPage lastPage;
    private final Player player;
    private final T trail;

    public ChooseParticleMenu(WbsTrails plugin, MenuPage lastPage, T trail, Player player) {
        this(plugin, lastPage, trail, player, 0);
    }
    public ChooseParticleMenu(WbsTrails plugin, MenuPage lastPage, T trail, Player player, int page) {
        super(plugin,
                plugin.settings.getAllowedParticlesFor(player).stream()
                        .sorted(Comparator.comparing(Particle::name))
                        .collect(Collectors.toList()),
                "&4&lChoose a particle!",
                "particle:" + player.getUniqueId(),
                1, // rowStart
                4, // maxRows
                1, // minColumn
                7, // maxColumn
                page);

        this.lastPage = lastPage;
        this.player = player;
        this.trail = trail;

        setUnregisterOnClose(true);

        setOutline(TrailMenuUtils.getOutlineSlot(), false);
        setSlot(0, 0, getBackSlot());
        setSlot(rows - 1, 4, TrailMenuUtils.getTrailPreview(trail, false));
    }

    @Override
    protected PageSlot<Particle> getSlot(Particle particle) {
        List<String> lore = new LinkedList<>();
        lore.add("&7Click to choose!");
        lore.add("&7(Shift+click to preview!)");

        String name = "&b" + WbsEnums.toPrettyString(particle);

        // Use super plugin since ChooseParticleMenu's constructor hasn't been called yet
        PageSlot<Particle> slot = new PageSlot<>(super.plugin,
                particle,
                Material.RED_DYE,
                super.plugin.dynamicColourise(name),
                true,
                super.plugin.colouriseAll(lore));

        slot.setClickAction(event -> {

            Class<?> dataType = particle.getDataType();
            DataProducer<?, ?> producer = DataManager.getProducer(dataType);

            if (event.isShiftClick()) {
                WbsParticleEffect effect = new NormalParticleEffect().setX(1).setY(2).setZ(1).setAmount(15);

                if (dataType != Void.class) {
                    if (producer != null) {
                        effect.setOptions(producer.produce());
                    } else {
                        plugin.sendMessage("&wData type not configured for this particle.", player);
                        return;
                    }
                }

                effect.play(particle, WbsEntityUtil.getMiddleLocation(player), player);
                return;
            }

            trail.setParticle(particle);

            goToOptions(particle);
        });

        return slot;
    }

    @Override
    public void showTo(Player player) {
        super.showTo(player);
        // Update trail whenever this is shown, rather than when it's created, as the player may come to this
        // via the back button
        setSlot(rows - 1, 4, TrailMenuUtils.getTrailPreview(trail, false));
        update(rows - 1, 4);

        Particle existingParticle = trail.getParticle();
        if (existingParticle != null) {
            MenuSlot useCurrentSlot = new MenuSlot(plugin,
                    Material.LIME_DYE,
                    "&a&lKeep Particle",
                    "&6Current: &b" + WbsEnums.toPrettyString(existingParticle)
            );

            useCurrentSlot.setClickAction(event -> goToOptions(existingParticle));

            setSlot(rows - 1, 8, useCurrentSlot);
            update(rows - 1, 8);
        }
    }

    private void goToOptions(Particle particle) {
        Class<?> dataType = particle.getDataType();
        DataProducer<?, ?> producer = DataManager.getProducer(dataType);

        if (producer == null) {
            if (dataType != Void.class) {
                plugin.sendMessage("&wA data type was missing &h(" + dataType.getCanonicalName() + ")&w. You can't use it right now.", player);
            } else {
                trail.setData(null);
                trail.build();
                plugin.runSync(() ->
                        new TrailOptionsMenu<>(plugin, this, trail, player).showTo(player));
            }
        } else {
            plugin.runSync(() -> {
                // If the player used the back button to get to this page, they may have
                // already set some values. Check before overriding.
                DataProducer<?, ?> existingProducer = trail.getData();

                if (existingProducer == null || existingProducer.getClass() != producer.getClass()) {
                    trail.setData(producer);
                    trail.build();
                    producer.getMenu(this, trail, player).showTo(player);
                } else {
                    existingProducer.getMenu(this, trail, player).showTo(player);
                }
            });
        }
    }

    @Override
    protected PagedMenu<Particle> getPage(int page) {
        return new ChooseParticleMenu<>(WbsTrails.getInstance(), lastPage, trail, player, page);
    }

    @Override
    public @Nullable MenuPage getLastPage() {
        return lastPage;
    }
}
