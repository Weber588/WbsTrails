package wbs.trails.command;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.WbsMath;
import wbs.utils.util.commands.WbsSubcommand;
import wbs.utils.util.entities.WbsEntityUtil;
import wbs.utils.util.plugin.WbsPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ImageCommand extends WbsSubcommand {
    public ImageCommand(@NotNull WbsPlugin plugin) {
        super(plugin, "image");
    }

    @Override
    protected boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, int start) {
        if (!(sender instanceof Player)) {
            sendMessage("Only usable by players.", sender);
            return true;
        }

        if (args.length < start + 1) {
            sendUsage("<fileName> [scale] [granularity] [particleSize]", sender, label, args);
            return true;
        }

        String fileName = args[start];

        double scale = 1;
        if (args.length >= start + 2) {
            try {
                scale = Double.parseDouble(args[start + 1]);
            } catch (NumberFormatException e) {
                sendMessage("Invalid number: " + args[start + 1], sender);
                return true;
            }

            if (scale <= 0) {
                sendMessage("Scale must be greater than 0.", sender);
                return true;
            }
        }

        int granularity = 1;
        if (args.length >= start + 3) {
            try {
                granularity = Integer.parseInt(args[start + 2]);
            } catch (NumberFormatException e) {
                sendMessage("Invalid integer: " + args[start + 2], sender);
                return true;
            }

            if (granularity <= 0) {
                sendMessage("Granularity must be greater than 0.", sender);
                return true;
            }
        }

        double particleSize = 1.5;
        if (args.length >= start + 4) {
            try {
                particleSize = Double.parseDouble(args[start + 3]);
            } catch (NumberFormatException e) {
                sendMessage("Invalid number: " + args[start + 3], sender);
                return true;
            }

            if (particleSize <= 0) {
                sendMessage("Scale must be greater than 0.", sender);
                return true;
            }
        }

        double finalScale = scale * 0.3; // Also scale by 0.3 to keep particles close at scale = 1
        int finalGranularity = granularity;
        double finalParticleSize = particleSize;
        plugin.runSync(() -> readImage(fileName, (Player) sender, finalScale, finalGranularity, finalParticleSize));

        return true;
    }

    private void readImage(String fileName, Player player, double scale, int granularity, double particleSize) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            sendMessage("&wFile not found: " + file.getPath(), player);
            return;
        }

        BufferedImage image;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            sendMessage("&wIOException occurred: \"&4" + e.getMessage() + "&w\"", player);
            e.printStackTrace();
            return;
        }

        sendMessage("&hFile loaded. Reading...", player);

        Map<Vector, Color> colorMap = new HashMap<>();
        int seed = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            if (y % granularity == 0) {
                seed++;
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgb = image.getRGB(x, y);
                    java.awt.Color color = new java.awt.Color(rgb, true);
                    if (color.getAlpha() != 0 && (x + seed) % granularity == 0) {
                        double xScaled = WbsMath.roundTo(x * scale, 3);
                        double yScaled = WbsMath.roundTo(y * scale, 3);
                        Color bukkitColor = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());

                        colorMap.put(new Vector(xScaled, yScaled, 0), bukkitColor);
                    }
                }
            }
        }

        String rawFileName = fileName.substring(0, fileName.lastIndexOf('.'));
        String coordsFileName = rawFileName + "-coords.yml";
        String folderPath = plugin.getDataFolder().getPath() + File.separator + "image-coords";

        File folder = new File(folderPath);
        boolean folderExists = true;
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                folderExists = false;
                sendMessage("&wFailed to create " + folderPath + ".", player);
            }
        }

        if (folderExists) {
            String path = folderPath + File.separator + coordsFileName;

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path), StandardCharsets.UTF_8))) {
                StringBuilder toWrite = new StringBuilder("  points:\n");
                for (Vector point : colorMap.keySet()) {
                    toWrite.append("    - '")
                            .append(point.getX())
                            .append(", ")
                            .append(point.getY())
                            .append(", ")
                            .append(point.getZ())
                            .append("'\n");
                }

                String writeString = toWrite.toString();

                writer.write(writeString);

                sendMessage("Created &h\"" + coordsFileName + "\"&r, listing coordinates of non-transparent pixels for use in a custom trail.", player);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        plugin.runSync(() -> {
            Location center = player.getEyeLocation().add(WbsEntityUtil.getFacingVector(player).multiply(3));
            Vector offset = new Vector(image.getWidth() * scale / 2, image.getHeight() * scale / 2, 0);

            World world = player.getWorld();

            Map<Vector, Color> rotated = new HashMap<>();

            Vector localUp = WbsEntityUtil.getLocalUp(player);
            Vector up = new Vector(0, 1, 0);

            offset = WbsMath.rotateFrom(offset, localUp, up);
            offset = WbsMath.rotateVector(offset, localUp, 0 - player.getLocation().getYaw());
            center.add(offset);

            for (Vector imagePos : colorMap.keySet()) {
                Vector rotatedPos = WbsMath.rotateFrom(imagePos, localUp, up);
                rotatedPos = WbsMath.rotateVector(rotatedPos, localUp, 0 - player.getLocation().getYaw());
                rotatedPos.multiply(-1);
                rotated.put(rotatedPos, colorMap.get(imagePos));
            }

            new BukkitRunnable() {
                int age = 0;
                @Override
                public void run() {
                    age++;
                    if (age > 15) {
                        cancel();
                        return;
                    }

                    for (Vector pos : rotated.keySet()) {
                        Particle.DustOptions data = new Particle.DustOptions(rotated.get(pos), (float) particleSize);

                        world.spawnParticle(Particle.REDSTONE, center.clone().add(pos), 0, data);
                    }
                }
            }.runTaskTimer(plugin, 5, 5);
        });
    }
}
