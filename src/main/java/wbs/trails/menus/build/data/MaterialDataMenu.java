package wbs.trails.menus.build.data;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wbs.trails.WbsTrails;
import wbs.trails.menus.TrailMenuUtils;
import wbs.trails.menus.build.MenuPage;
import wbs.trails.menus.build.TrailOptionsMenu;
import wbs.trails.trails.Trail;
import wbs.trails.trails.data.MaterialDataProducer;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.PageSlot;
import wbs.utils.util.menus.PagedMenu;
import wbs.utils.util.particles.NormalParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;

import java.util.LinkedList;
import java.util.List;

public class MaterialDataMenu<T extends Trail<T>> extends PagedMenu<Material> implements MenuPage {

    private final MenuPage lastPage;
    private final MaterialDataProducer<?, ?> producer;
    private final T trail;
    private final Player player;

    public MaterialDataMenu(WbsTrails plugin, MenuPage lastPage, MaterialDataProducer<?, ?> producer, T trail, Player player) {
        this(plugin, lastPage, producer, trail, player, 0);
    }

    public MaterialDataMenu(WbsTrails plugin, MenuPage lastPage, MaterialDataProducer<?, ?> producer, T trail, Player player, int page) {
        super(plugin,
                producer.getValidMaterials(),
                "&6Customize Material Data!",
                "material_data:" + player.getUniqueId(),
                1, // rowStart
                4, // maxRows
                1, // minColumn
                7, // maxColumn
                page);

        this.lastPage = lastPage;
        this.producer = producer;
        this.trail = trail;
        this.player = player;

        setUnregisterOnClose(true);

        setOutline(TrailMenuUtils.getOutlineSlot(), false);

        setSlot(0, 0, getBackSlot());
    }

    @Override
    public void showTo(Player player) {
        super.showTo(player);
        // Update trail whenever this is shown, rather than when it's created, as the player may come to this
        // via the back button
        setSlot(rows - 1, 4, TrailMenuUtils.getTrailPreview(trail, false));
        update(rows - 1, 4);

        Material current = producer.getMaterial();
        if (current != null) {
            MenuSlot useCurrentSlot = new MenuSlot(plugin,
                    current,
                    "&a&lKeep Material",
                    "&6Current: &b" + WbsEnums.toPrettyString(current)
            );

            useCurrentSlot.setClickAction(event -> goToOptions(current));

            setSlot(rows - 1, 8, useCurrentSlot);
            update(rows - 1, 8);
        }
    }

    @Override
    protected PageSlot<Material> getSlot(Material material) {
        List<String> lore = new LinkedList<>();
        lore.add("&7Click to choose!");
        lore.add("&7(Shift+click to preview!)");

        String name = "&b" + WbsEnums.toPrettyString(material);

        PageSlot<Material> slot = new PageSlot<>(super.plugin,
                material,
                material.isItem() ? material : Material.BARRIER,
                super.plugin.dynamicColourise(name),
                super.plugin.colouriseAll(lore));

        slot.setClickAction(event -> {
            if (event.isShiftClick()) {
                WbsParticleEffect effect = new NormalParticleEffect().setX(1).setY(2).setZ(1).setAmount(15);

                effect.setOptions(producer.produce(material));

                effect.play(trail.getParticle(), WbsEntityUtil.getMiddleLocation(player), player);
                return;
            }

            goToOptions(material);
        });

        return slot;
    }

    public void goToOptions(Material material) {
        producer.setMaterial(material);
        trail.build();

        plugin.runSync(() ->
                new TrailOptionsMenu<>(plugin, this, trail, player).showTo(player));
    }

    @Override
    protected PagedMenu<Material> getPage(int page) {
        return new MaterialDataMenu<>(WbsTrails.getInstance(), lastPage, producer, trail, player, page);
    }

    @Override
    public @Nullable MenuPage getLastPage() {
        return lastPage;
    }
}
