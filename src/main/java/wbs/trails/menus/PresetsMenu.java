package wbs.trails.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import wbs.trails.TrailsController;
import wbs.trails.trails.Trail;
import wbs.trails.trails.data.DataProducer;
import wbs.trails.trails.options.ConfigOption;
import wbs.trails.trails.options.OptionPair;
import wbs.trails.trails.presets.PresetManager;
import wbs.trails.trails.presets.PresetTrail;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.PageSlot;
import wbs.utils.util.menus.PagedMenu;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.string.WbsStrings;

import java.util.LinkedList;
import java.util.List;

public class PresetsMenu extends PagedMenu<PresetTrail<?>> {

    private final Player player;

    public PresetsMenu(WbsPlugin plugin, Player player) {
        this(plugin, player, 0);
    }

    public PresetsMenu(WbsPlugin plugin, Player player, int page) {
        super(plugin,
                PresetManager.getAllowed(player),
                "&d&lChoose a Trail!",
                "preset_menu:" + player.getUniqueId(),
                1,
                4,
                1,
                7,
                page);

        this.player = player;

        setUnregisterOnClose(true);
        setOutline(TrailMenuUtils.getOutlineSlot(), false);
        setSlot(0, 0, TrailMenuUtils.getMainMenuLink());
    }

    @Override
    protected PageSlot<PresetTrail<?>> getSlot(PresetTrail<?> presetTrail) {
        List<String> lore = new LinkedList<>();

        String lineBreak = "&b&m                          ";
        lore.add(lineBreak);

        String description = presetTrail.getDescription();
        if (description != null) {
            String[] words = presetTrail.getDescription().split(" ");
            StringBuilder currentLine = new StringBuilder();
            int lineLength = 0;
            for (String word : words) {
                final int MAX_LINE_LENGTH = 20;
                if (word.length() >= MAX_LINE_LENGTH && !currentLine.toString().equals("")) {
                    lore.add(word);
                    continue;
                }

                lineLength += word.length() + 1;

                if (lineLength > MAX_LINE_LENGTH) {
                    lineLength = 0;
                    lore.add("&7" + currentLine);
                    currentLine = new StringBuilder(word).append(" ");
                } else {
                    currentLine.append(word).append(" ");
                }
            }

            if (!currentLine.toString().isEmpty()) {
                lore.add("&7" + currentLine);
            }
        } else {
            lore.add("&6Particle&7: &b" + WbsEnums.toPrettyString(presetTrail.getParticle()));

            if (presetTrail.getData() != null) {
                DataProducer<?, ?> producer = presetTrail.getData();

                for (String line : producer.getValueDisplays()) {
                    lore.add("  " + line);
                }
            }

            for (OptionPair<?, ?> pair : presetTrail.getOptions()) {
                lore.add("&6" + WbsStrings.capitalize(pair.getOption().getName()) + "&7: &b" + pair.getValue());
            }
        }

        lore.add(lineBreak);

        String name = presetTrail.getName();
        name = name.replace("_", " ");
        name = WbsStrings.capitalizeAll(name);
        name = plugin.dynamicColourise("&b" + name);

        PageSlot<PresetTrail<?>> slot = new PageSlot<>(plugin,
                presetTrail,
                presetTrail.getRegistration().getMaterial(),
                name,
                true,
                plugin.colouriseAll(lore));

        slot.setClickAction(event -> {
            Player player = (Player) event.getWhoClicked();

            if (!presetTrail.hasPermission(player)) {
                plugin.sendMessage("&wYou don't have permission to use that trail.", player);
                return;
            }

            TrailsController controller = TrailsController.getInstance();
            Trail<?> trail = presetTrail.getTrail(player);
            if (controller.tryAddTrail(player, trail)) {
                trail.enable();

                plugin.sendMessage("Trail enabled!", player);
            }
        });

        return slot;
    }

    @Override
    protected PagedMenu<PresetTrail<?>> getPage(int page) {
        return new PresetsMenu(plugin, player, page);
    }
}
