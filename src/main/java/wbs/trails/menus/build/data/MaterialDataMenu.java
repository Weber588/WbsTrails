package wbs.trails.menus.build.data;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wbs.trails.WbsTrails;
import wbs.trails.menus.TrailMenuUtils;
import wbs.trails.menus.build.BuildMenu;
import wbs.trails.menus.build.ChooseParticleMenu;
import wbs.trails.menus.build.TrailOptionsMenu;
import wbs.trails.trails.Trail;
import wbs.trails.trails.data.DataManager;
import wbs.trails.trails.data.DataProducer;
import wbs.trails.trails.data.MaterialDataProducer;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.PagedMenu;
import wbs.utils.util.menus.WbsMenu;
import wbs.utils.util.particles.NormalParticleEffect;
import wbs.utils.util.particles.WbsParticleEffect;

import java.util.LinkedList;
import java.util.List;

public class MaterialDataMenu<T extends Trail<T>> extends PagedMenu<Material> implements BuildMenu {

    private final BuildMenu lastPage;
    private final MaterialDataProducer<?, ?> producer;
    private final T trail;
    private final Player player;

    public MaterialDataMenu(WbsTrails plugin, BuildMenu lastPage, MaterialDataProducer<?, ?> producer, T trail, Player player) {
        this(plugin, lastPage, producer, trail, player, 0);
    }

    public MaterialDataMenu(WbsTrails plugin, BuildMenu lastPage, MaterialDataProducer<?, ?> producer, T trail, Player player, int page) {
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

        setSlot(rows - 1, 1, getBackSlot());
    }

    @Override
    protected MenuSlot getSlot(Material material) {
        List<String> lore = new LinkedList<>();
        lore.add("&7Click to choose!");
        lore.add("&7(Shift+click to preview!)");

        String name = "&b" + WbsEnums.toPrettyString(material);

        MenuSlot slot = new MenuSlot(super.plugin,
                material.isItem() ? material : Material.BARRIER,
                super.plugin.dynamicColourise(name),
                super.plugin.colouriseAll(lore));

        slot.setClickAction(event -> {

            producer.setMaterial(material);

            if (event.isShiftClick()) {
                WbsParticleEffect effect = new NormalParticleEffect().setX(1).setY(2).setZ(1).setAmount(15);

                effect.setOptions(producer.produce());

                effect.play(trail.getParticle(), WbsEntityUtil.getMiddleLocation(player), player);
                return;
            }

            plugin.runSync(() ->
                    new TrailOptionsMenu<>(plugin, this, trail, player).showTo(player));
        });

        return slot;
    }


    @Override
    protected PagedMenu<Material> getPage(int page) {
        return new MaterialDataMenu<>(WbsTrails.getInstance(), lastPage, producer, trail, player, page);
    }

    @Override
    public @Nullable BuildMenu getLastPage() {
        return lastPage;
    }
}
