package wbs.trails.menus.build.data;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wbs.trails.WbsTrails;
import wbs.trails.menus.TrailMenuUtils;
import wbs.trails.menus.build.BuildMenu;
import wbs.trails.menus.build.TrailOptionsMenu;
import wbs.trails.trails.Trail;
import wbs.trails.trails.data.DustDataProducer;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.WbsMenu;

public class DustDataMenu<T extends Trail<T>> extends WbsMenu implements BuildMenu {

    private final BuildMenu lastPage;

    public DustDataMenu(WbsTrails plugin, BuildMenu lastPage, DustDataProducer producer, T trail, Player player) {
        super(plugin, "&4Customize Dust Data!", 3, "dust_data:" + player.getUniqueId());

        this.lastPage = lastPage;

        setUnregisterOnClose(true);

        setOutline(TrailMenuUtils.getOutlineSlot());

        // TODO: Custom make the slots for these, since the numbers are big and need a better control method
        MenuSlot redSlot = producer.getRedOption().newSlot();
        redSlot.getItem().setType(Material.RED_WOOL);

        MenuSlot greenSlot = producer.getGreenOption().newSlot();
        greenSlot.getItem().setType(Material.GREEN_WOOL);

        MenuSlot blueSlot = producer.getBlueOption().newSlot();
        blueSlot.getItem().setType(Material.BLUE_WOOL);

        setNextFreeSlot(redSlot);
        setNextFreeSlot(greenSlot);
        setNextFreeSlot(blueSlot);

        // TODO: Create rainbow toggle in the outline somewhere (if player has perm) that replaces RGB slots with
        //  rainbowSpeed, saturation (gray wool?), and brightness (yellow wool? torch/other light source?)

        MenuSlot doneSlot = new MenuSlot(plugin, Material.LIME_DYE, "&a&lDone");

        doneSlot.setClickAction(event -> {
            // Don't need to update producer with each slot; producer options are self referential to creating object

            trail.setData(producer);

            plugin.runSync(() -> new TrailOptionsMenu<>(plugin, this, trail, player).showTo(player));
        });

        setSlot(rows - 1, 7, doneSlot);

        setSlot(rows - 1, 1, getBackSlot());
    }

    @Override
    public @Nullable BuildMenu getLastPage() {
        return lastPage;
    }
}
