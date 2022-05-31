package wbs.trails.menus.build;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wbs.trails.WbsTrails;
import wbs.utils.util.menus.MenuSlot;
import wbs.utils.util.menus.WbsMenu;

public interface MenuPage {
    @Nullable
    MenuPage getLastPage();

    default void back(Player player) {
        MenuPage lastPage = getLastPage();

        if (lastPage != null) {
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
