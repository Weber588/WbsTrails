package wbs.trails.menus.build;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wbs.trails.WbsTrails;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.WbsMenu;

public interface BuildMenu {
    @Nullable
    BuildMenu getLastPage();

    default void back(Player player) {
        BuildMenu lastPage = getLastPage();

        if (lastPage != null) {
            if (this instanceof WbsMenu) {
                ((WbsMenu) this).unregister();
            }

            if (lastPage instanceof WbsMenu) {
                WbsTrails.getInstance().runSync(
                        () -> ((WbsMenu) lastPage).showTo(player)
                );
            }
        }
    }

    default MenuSlot getBackSlot() {
        MenuSlot backSlot = new MenuSlot(WbsTrails.getInstance(), Material.CLOCK, "&cBack");

        backSlot.setClickAction(event -> back((Player) event.getWhoClicked()));

        return backSlot;
    }
}
