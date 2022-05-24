package wbs.trails.menus.build;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import wbs.trails.WbsTrails;
import wbs.trails.menus.PlayerSpecificMenu;
import wbs.trails.menus.TrailMenuUtils;
import wbs.trails.trails.RegisteredTrail;
import wbs.trails.trails.TrailManager;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.PagedMenu;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.string.WbsStrings;

import java.util.LinkedList;
import java.util.List;

public class ChooseTrailMenu extends PagedMenu<RegisteredTrail<?>> {

    private final Player player;

    public ChooseTrailMenu(WbsPlugin plugin, Player player) {
        this(plugin, player, 0);
    }

    public ChooseTrailMenu(WbsPlugin plugin, Player player, int page) {
        super(plugin,
                TrailManager.getAllowed(player),
                "&3&lChoose Trail Type!",
                "create:" + player.getUniqueId(),
                1,
                4,
                1,
                7,
                page);
        this.player = player;

        setUnregisterOnClose(true);
        setOutline(TrailMenuUtils.getOutlineSlot());
    }

    private final int LINE_LENGTH = 20;

    @Override
    protected MenuSlot getSlot(RegisteredTrail<?> registeredTrail) {
        List<String> lore = new LinkedList<>();

        String[] words = registeredTrail.getDescription().split(" ");
        StringBuilder currentLine = new StringBuilder();
        int lineLength = 0;
        for (String word : words) {
            if (word.length() >= LINE_LENGTH && !currentLine.toString().equals("")) {
                lore.add(word);
                continue;
            }

            lineLength += word.length() + 1;

            if (lineLength > LINE_LENGTH) {
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

        String name = registeredTrail.getName();
        name = name.replace("_", " ");
        name = WbsStrings.capitalizeAll(name);
        name = plugin.dynamicColourise("&b" + name);

        MenuSlot slot = new MenuSlot(plugin, Material.OAK_SIGN, name, plugin.colouriseAll(lore));

        slot.setClickAction(event -> {
            new ChooseParticleMenu(WbsTrails.getInstance(), registeredTrail, player).showTo(player);
        });

        return slot;
    }

    @Override
    protected PagedMenu<RegisteredTrail<?>> getPage(int page) {
        return new ChooseTrailMenu(plugin, player, page);
    }
}