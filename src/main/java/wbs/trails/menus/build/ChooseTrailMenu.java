package wbs.trails.menus.build;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wbs.trails.WbsTrails;
import wbs.trails.menus.TrailMenuUtils;
import wbs.trails.trails.RegisteredTrail;
import wbs.trails.trails.Trail;
import wbs.trails.trails.TrailManager;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.PagedMenu;
import wbs.utils.util.plugin.WbsPlugin;
import wbs.utils.util.string.WbsStrings;

import java.util.LinkedList;
import java.util.List;

public class ChooseTrailMenu extends PagedMenu<RegisteredTrail<?>> implements BuildMenu {

    private final Player player;

    public ChooseTrailMenu(WbsPlugin plugin, Player player) {
        this(plugin, player, 0);
    }

    public ChooseTrailMenu(WbsPlugin plugin, Player player, int page) {
        super(plugin,
                TrailManager.getAllowed(player),
                "&3&lChoose Trail Type!",
                "create:" + player.getUniqueId(),
                1, // rowStart
                4, // maxRows
                1, // minColumn
                7, // maxColumn
                page);
        this.player = player;

        setUnregisterOnClose(true);
        setOutline(TrailMenuUtils.getOutlineSlot());
    }

    @Override
    protected MenuSlot getSlot(RegisteredTrail<?> registeredTrail) {
        List<String> lore = new LinkedList<>();

        String[] words = registeredTrail.getDescription().split(" ");
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

        String name = registeredTrail.getName();
        name = name.replace("_", " ");
        name = WbsStrings.capitalizeAll(name);
        name = plugin.dynamicColourise("&b" + name);

        MenuSlot slot = new MenuSlot(plugin, Material.OAK_SIGN, name, plugin.colouriseAll(lore));

        slot.setClickAction(event -> choose(registeredTrail));

        return slot;
    }

    private <T extends Trail<T>> void choose(RegisteredTrail<T> registration) {
        T trail = registration.buildTrail(player);

        plugin.runSync(() ->
                new ChooseParticleMenu<>(WbsTrails.getInstance(), this, trail, player).showTo(player));
    }

    @Override
    protected PagedMenu<RegisteredTrail<?>> getPage(int page) {
        return new ChooseTrailMenu(plugin, player, page);
    }

    @Override
    public @Nullable BuildMenu getLastPage() {
        return null;
    }
}