package wbs.trails.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.trails.WbsTrails;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class ChatStringReader implements Listener {

    public static final String CANCEL_PHRASE = "cancel";
    
    private static final Map<Player, Reader> readers = new HashMap<>();

    private static void unregister(Reader reader) {
        readers.remove(reader.player, reader);
    }

    public static boolean getStringFromChat(Player player, Consumer<String> consumer) {
        return getStringFromChat(player, consumer, null, 0);
    }

    public static boolean getStringFromChat(Player player, Consumer<String> consumer, Runnable onTimeout, int timeout) {
        if (readers.containsKey(player)) return false;

        Reader reader = new Reader(player, consumer, onTimeout, timeout);

        readers.put(player, reader);

        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Reader reader = readers.get(event.getPlayer());
        if (reader != null) {
            event.setCancelled(true);
            if (event.getMessage().equalsIgnoreCase(CANCEL_PHRASE)) {
                unregister(reader);
                WbsTrails.getInstance().sendMessage("&wCancelled!", event.getPlayer());
            } else {
                WbsTrails.getInstance().runSync(() -> reader.consume(event.getMessage()));
            }
        }
    }

    private static class Reader {
        @NotNull
        private final Player player;
        @NotNull
        private final Consumer<String> consumer;
        private int timeoutRunnableId = -1;

        public Reader(@NotNull Player player, @NotNull Consumer<String> consumer, @Nullable Runnable timeoutRunnable, int timeoutInTicks) {
            this.player = player;
            this.consumer = consumer;

            if (timeoutRunnable != null) {
                if (timeoutInTicks <= 0)
                    throw new IllegalArgumentException("timeout duration must be greater than or equal to 0");

                timeoutRunnableId = new BukkitRunnable() {
                    @Override
                    public void run() {
                        unregister(Reader.this);
                        timeoutRunnable.run();
                    }
                }.runTaskLater(WbsTrails.getInstance(), timeoutInTicks).getTaskId();
            }
        }

        public void consume(String message) {
            unregister(this);
            consumer.accept(message);
            if (timeoutRunnableId != -1) {
                Bukkit.getScheduler().cancelTask(timeoutRunnableId);
            }
        }
    }
}
