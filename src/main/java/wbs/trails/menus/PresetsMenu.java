package wbs.trails.menus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import wbs.trails.TrailsController;
import wbs.trails.trails.Trail;
import wbs.trails.trails.data.DataProducer;
import wbs.trails.trails.options.ConfigOption;
import wbs.trails.trails.options.OptionPair;
import wbs.trails.trails.presets.PresetGroup;
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

public class PresetsMenu extends PagedMenu<PresetGroup> {

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
    protected PageSlot<PresetGroup> getSlot(PresetGroup group) {
        List<String> lore = new LinkedList<>();

        String lineBreak = "&b&m                          ";
        lore.add(lineBreak);

        String description = group.getDescription();
        if (description != null) {
            String[] words = group.getDescription().split(" ");
            StringBuilder currentLine = new StringBuilder();
            int lineLength = 0;
            String lastLine = "&7 ";
            for (String word : words) {
                final int MAX_LINE_LENGTH = 20;
                if (word.length() >= MAX_LINE_LENGTH && !currentLine.toString().equals("")) {
                    lore.add(word);
                    continue;
                }

                lineLength += word.length() + 1;

                if (lineLength > MAX_LINE_LENGTH) {
                    lineLength = 0;
                    String lastColours = ChatColor.getLastColors(plugin.dynamicColourise(lastLine));
                    currentLine.insert(0, lastColours);
                    lore.add(currentLine.toString());

                    lastLine = currentLine.toString();
                    currentLine = new StringBuilder(word).append(" ");
                } else {
                    currentLine.append(word).append(" ");
                }
            }

            if (!currentLine.toString().isEmpty()) {
                String lastColours = ChatColor.getLastColors(plugin.dynamicColourise(lastLine));
                lore.add(lastColours + currentLine);
            }
        } else {
            for (PresetTrail<?> trail : group.getTrails()) {
                lore.add("  &6" + WbsStrings.capitalizeAll(trail.getRegistration().getName()) + " (" + WbsEnums.toPrettyString(trail.getParticle()) + ")");
            }
        }

        lore.add(lineBreak);

        String name = group.getName();
        name = plugin.dynamicColourise("&b" + name);

        PageSlot<PresetGroup> slot = new PageSlot<>(plugin,
                group,
                group.getMaterial(),
                name,
                true,
                plugin.colouriseAll(lore));

        slot.setClickAction(event -> {
            Player player = (Player) event.getWhoClicked();

            if (!group.hasPermission(player)) {
                plugin.sendMessage("&wYou don't have permission to use that trail.", player);
                return;
            }

            group.apply(player);
            plugin.sendMessage("Preset enabled!", player);
        });

        return slot;
    }

    @Override
    protected PagedMenu<PresetGroup> getPage(int page) {
        return new PresetsMenu(plugin, player, page);
    }
}
