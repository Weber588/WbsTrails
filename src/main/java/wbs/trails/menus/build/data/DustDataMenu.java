package wbs.trails.menus.build.data;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.trails.WbsTrails;
import wbs.trails.menus.TrailMenuUtils;
import wbs.trails.menus.build.MenuPage;
import wbs.trails.menus.build.TrailOptionsMenu;
import wbs.trails.menus.build.options.DoubleOptionSlot;
import wbs.trails.trails.Trail;
import wbs.trails.trails.data.DustDataProducer;
import wbs.utils.util.WbsColours;
import wbs.utils.util.WbsMath;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.WbsMenu;
import wbs.utils.util.string.WbsStrings;

import java.util.Arrays;

public class DustDataMenu<T extends Trail<T>> extends WbsMenu implements MenuPage {

    private final int PREVIEW_SLOT = getSlotNumber(0, 4);

    private final int RED_COLUMN = 1;
    private final int GREEN_COLUMN = 2;
    private final int BLUE_COLUMN = 3;
    private final int SIZE_COLUMN = 4;

    private final MenuPage lastPage;
    private final DustDataProducer producer;
    private final T trail;
    @NotNull
    private Color currentColour;
    private final MenuSlot previewSlot;

    private final MenuSlot red;
    private final MenuSlot green;
    private final MenuSlot blue;

    public DustDataMenu(WbsTrails plugin, MenuPage lastPage, DustDataProducer producer, T trail, Player player) {
        super(plugin, "&4Customize Dust Data!", 3, "dust_data:" + player.getUniqueId());

        this.lastPage = lastPage;
        this.producer = producer;
        this.trail = trail;

        trail.setData(producer);

        currentColour = Color.fromRGB(producer.getRed(), producer.getGreen(), producer.getBlue());

        setUnregisterOnClose(true);

        setOutline(TrailMenuUtils.getOutlineSlot());

        previewSlot = new MenuSlot(plugin, Material.LEATHER_CHESTPLATE, ChatColor.DARK_RED + "Preview");
        setSlot(PREVIEW_SLOT, previewSlot);
        updatePreviewSlot();

        red = new MenuSlot(plugin, Material.RED_WOOL, "&cRed");
        red.setClickAction(event -> {
            int val = update(event, producer.getRed());
            producer.setRed(val);

            currentColour = currentColour.setRed(val);

            updatePreviewSlot();
            updateRGB();
        });

        green = new MenuSlot(plugin, Material.GREEN_WOOL, "&aGreen");
        green.setClickAction(event -> {
            int val = update(event, producer.getGreen());
            producer.setGreen(val);

            currentColour = currentColour.setGreen(val);

            updatePreviewSlot();
            updateRGB();
        });

        blue = new MenuSlot(plugin, Material.BLUE_WOOL, "&bBlue");
        blue.setClickAction(event -> {
            int val = update(event, producer.getBlue());
            producer.setBlue(val);

            currentColour = currentColour.setBlue(val);

            updatePreviewSlot();
            updateRGB();
        });

        DoubleOptionSlot<DustDataProducer> size = new DoubleOptionSlot<>(plugin, producer.getSizeOption());
        size.getItem().setType(Material.STONE_BUTTON);
        size.fromT(producer);
        size.setOnValueChange(value -> {
            size.getPair().apply(producer);
            setSlot(rows - 1, 4, TrailMenuUtils.getTrailPreview(trail, false));
        });

        setSlot(1, RED_COLUMN, red);
        setSlot(1, GREEN_COLUMN, green);
        setSlot(1, BLUE_COLUMN, blue);
        setSlot(1, SIZE_COLUMN, size);

        updateRGB();

        // TODO: Create rainbow toggle in the outline somewhere (if player has perm) that replaces RGB slots with
        //  rainbowSpeed, saturation (gray wool?), and brightness (yellow wool? torch/other light source?)

        MenuSlot doneSlot = new MenuSlot(plugin, Material.LIME_DYE, "&a&lDone");

        doneSlot.setClickAction(event -> {
            // Don't need to update producer with each slot; producer options are self referential to creating object

            trail.build();

            plugin.runSync(() -> new TrailOptionsMenu<>(plugin, this, trail, player).showTo(player));
        });

        setSlot(rows - 1, 8, doneSlot);
        setSlot(0, 0, getBackSlot());
    }

    @Override
    public void showTo(Player player) {
        super.showTo(player);
        // Update trail whenever this is shown, rather than when it's created, as the player may come to this
        // via the back button
        setSlot(rows - 1, 4, TrailMenuUtils.getTrailPreview(trail, false));
        update(rows - 1, 4);
    }

    private void updatePreviewSlot() {
        ItemMeta meta = previewSlot.getItem().getItemMeta();
        assert meta != null;

        meta.setDisplayName(WbsStrings.colourise(WbsColours.toChatColour(currentColour) + "Preview"));

        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta armourMeta = (LeatherArmorMeta) meta;
            armourMeta.setColor(currentColour);
        }

        previewSlot.getItem().setItemMeta(meta);
        update(PREVIEW_SLOT);
    }

    @Override
    public @Nullable MenuPage getLastPage() {
        return lastPage;
    }

    private int update(InventoryClickEvent event, int current) {
        switch (event.getClick()) {
            case LEFT:
            case SHIFT_LEFT:
                current += 10;
                break;
            case RIGHT:
            case SHIFT_RIGHT:
                current -= 10;
                break;
            case MIDDLE:
                current = 130;
                break;
            case NUMBER_KEY: // Set to fraction of number key, i.e. 1 = 0, 9 = 255
                current = (int) (event.getHotbarButton() * (255 / 8.0));
                break;
            case DROP:
            case CONTROL_DROP: // random
                current = (int) (Math.random() * 255);
                break;
        }

        current = WbsMath.clamp(0, 255, current);

        return current;
    }

    private void updateRGB() {
        updateRGBSlot(red, getSlotNumber(1, RED_COLUMN), producer.getRed());
        updateRGBSlot(green, getSlotNumber(1, GREEN_COLUMN), producer.getGreen());
        updateRGBSlot(blue, getSlotNumber(1, BLUE_COLUMN), producer.getBlue());

        setSlot(rows - 1, 4, TrailMenuUtils.getTrailPreview(trail, false));
        update(rows - 1, 4);
    }

    private void updateRGBSlot(MenuSlot slot, int slotNum, int value) {
        ItemMeta meta = slot.getItem().getItemMeta();
        assert meta != null;

        meta.setLore(Arrays.asList("&6Current: &b" + value,
                "&7Left click: &b+10",
                "&7Right click: &b-10",
                "&7Q: &bRandom",
                WbsColours.toChatColour(currentColour) + "&lPreview"));

        slot.getItem().setItemMeta(meta);
        update(slotNum);
    }

}
