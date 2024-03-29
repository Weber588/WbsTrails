package wbs.trails.trails.data;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import wbs.trails.WbsTrails;
import wbs.trails.menus.build.MenuPage;
import wbs.trails.menus.build.data.DustDataMenu;
import wbs.trails.trails.Trail;
import wbs.trails.trails.options.ConfigOption;
import wbs.trails.trails.options.DoubleOption;
import wbs.utils.util.WbsColours;
import wbs.utils.util.menus.WbsMenu;
import wbs.utils.util.particles.data.DustOptionsProvider;
import wbs.utils.util.providers.NumProvider;
import wbs.utils.util.providers.VectorProvider;
import wbs.utils.util.providers.generator.num.CycleGenerator;
import wbs.utils.util.string.WbsStrings;

import java.util.*;

public class DustDataProducer extends DataProducer<Particle.DustOptions, DustDataProducer> {

    public static DustDataProducer deserialize(ConfigurationSection section, String path) {
        DustDataProducer created = new DustDataProducer();

        created.red = section.getInt(path + ".red");
        created.green = section.getInt(path + ".green");
        created.blue = section.getInt(path + ".blue");
        created.size = (float) section.getDouble(path + ".size");

        created.rainbow = section.getBoolean(path + ".rainbow");

        created.rainbowSpeed = section.getDouble(path + ".rainbow-speed");
        created.saturation = section.getDouble(path + ".saturation");
        created.brightness = section.getDouble(path + ".brightness");

        return created;
    }

    private static final String RAINBOW_STRING = "rainbow";

    private int red = 255;
    private int green = 0;
    private int blue = 0;

    private float size = 1;

    // Rainbow specific options
    private boolean rainbow = false;
    private double rainbowSpeed = 1;
    private double saturation = 1;
    private double brightness = 1;

    @Override
    @NotNull
    public Particle.DustOptions produce() {
        Particle.DustOptions data;

        if (rainbow) {
            CycleGenerator period = new CycleGenerator(0, 1, 100 / (rainbowSpeed * WbsTrails.getInstance().settings.getRefreshRate()), 0);
            NumProvider hue = new NumProvider(period);
            VectorProvider HSBProvider = new VectorProvider(hue, new NumProvider(saturation), new NumProvider(brightness));
            data = new DustOptionsProvider(HSBProvider, size, DustOptionsProvider.ColourType.HSB);
        } else {
            data = new DustOptionsProvider(new VectorProvider(red, green, blue),
                    new NumProvider(size),
                    DustOptionsProvider.ColourType.INT255);
        }

        return data;
    }

    @Override
    public int configure(String[] args) throws IllegalArgumentException {
        if (args.length < 2) {
            throw new IllegalArgumentException("This particle requires additional data: " + getUsage());
        }

        if (args[0].equalsIgnoreCase(RAINBOW_STRING)) {
            rainbow = true;

            size = -1;
            try {
                size = Float.parseFloat(args[1]);
            } catch (NumberFormatException ignored) {}

            if (size < 0 || size > 2) {
                throw new IllegalArgumentException("Invalid size. Size must be between 0 and 2 inclusive.");
            }

            return 2;
        }

        red = parseIntSafely(args[0]);
        if (red < 0 || red > 255) {
            throw new IllegalArgumentException("Invalid red value; must be an int between 0 and 255 inclusive.");
        }
        green = parseIntSafely(args[1]);
        if (green < 0 || green > 255) {
            throw new IllegalArgumentException("Invalid green value; must be an int between 0 and 255 inclusive.");
        }
        blue = parseIntSafely(args[2]);
        if (blue < 0 || blue > 255) {
            throw new IllegalArgumentException("Invalid blue value; must be an int between 0 and 255 inclusive.");
        }

        size = -1;
        try {
            size = Float.parseFloat(args[3]);
        } catch (NumberFormatException ignored) {}

        if (size < 0 || size > 2) {
            throw new IllegalArgumentException("Invalid size. Size must be between 0 and 2 inclusive.");
        }

        return 4;
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        section.set(path + ".red", red);
        section.set(path + ".green", green);
        section.set(path + ".blue", blue);

        section.set(path + ".rainbow", rainbow);
        section.set(path + ".rainbow-speed", rainbowSpeed);
        section.set(path + ".saturation", saturation);
        section.set(path + ".brightness", brightness);

        section.set(path + ".size", size);
    }

    @Override
    public Collection<ConfigOption<DustDataProducer, ?>> getDataOptions() {
        List<ConfigOption<DustDataProducer, ?>> options = new LinkedList<>();

        if (rainbow) {
            options.add(getRainbowSpeedOption());

            options.add(getSaturationOption());

            options.add(getBrightnessOption());
        }
        return options;
    }

    @Override
    public Class<Particle.DustOptions> getDataClass() {
        return Particle.DustOptions.class;
    }

    @Override
    public <T extends Trail<T>> WbsMenu getMenu(MenuPage lastPage, T trail, Player player) {
        return new DustDataMenu<>(WbsTrails.getInstance(), lastPage, this, trail, player);
    }

    @Override
    public Collection<String> getValueDisplays() {
        List<String> lines = new LinkedList<>();
        if (rainbow) {
            Color start = WbsColours.fromHSB(0, 1 * saturation, 1 * brightness);
            Color end = WbsColours.fromHSB(0.957, 1 * saturation, 0.502 * brightness);
            String rainbowString = WbsStrings.addColourGradient("Rainbow", start, end);
            lines.add(rainbowString + "&7: &bTrue");
            lines.add("&6Saturation&7: &b" + saturation);
            lines.add("&6Brightness&7: &b" + brightness);
        } else {
            String preview = "&7(" + WbsColours.toChatColour(Color.fromRGB(red, green, blue)) + "Preview&7)";
            lines.add("&6Colour&7: &c" + red + "&7, &a" + green + "&7, &b" + blue + " " + preview);
        }

        lines.add("&6Size&7: &b" + size);

        return lines;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public DoubleOption<DustDataProducer> getRainbowSpeedOption() {
        return new DoubleOption<>("rainbow_speed", 1, 0.2, 10,
                (producer, newValue) -> producer.rainbowSpeed = newValue,
                producer -> producer.rainbowSpeed);
    }

    public DoubleOption<DustDataProducer> getSaturationOption() {
        return new DoubleOption<>("saturation", 1, 0, 1,
                (producer, newValue) -> producer.saturation = newValue,
                producer -> producer.saturation);
    }

    public DoubleOption<DustDataProducer> getBrightnessOption() {
        return new DoubleOption<>("brightness", 1, 0, 1,
                (producer, newValue) -> producer.brightness = newValue,
                producer -> producer.brightness);
    }

    public DoubleOption<DustDataProducer> getSizeOption() {
        return new DoubleOption<>("size", 1, 0.1, 2,
                (producer, newValue) -> producer.size = newValue.floatValue(),
                producer -> (double) producer.size);
    }

    @Override
    @Nullable
    public List<String> handleTab(String[] args) {
        if (args.length == 0) return null;

        List<String> choices = new LinkedList<>();

        if (args.length == 1) {
            choices.add("0");
            choices.add("255");
            choices.add(RAINBOW_STRING);
            return choices;
        }

        if (args[0].equalsIgnoreCase(RAINBOW_STRING)) {
            if ((args.length == 2)) {
                choices.add("0.5");
                choices.add("1");
                choices.add("1.5");
                choices.add("2");
                return choices;
            }

            return null;
        } else {
            if (args.length < 4) {
                choices.add("0");
                choices.add("255");
                return choices;
            } else if (args.length == 4) {
                choices.add("0.5");
                choices.add("1");
                choices.add("1.5");
                choices.add("2");
                return choices;
            } else {
                return null;
            }
        }
    }

    @Override
    public String getUsage() {
        return "<[<red> <green> <blue>]|rainbow> <size>";
    }

    /**
     * Parse and swallow errors from String to int,
     * returning a magic number if errored.
     * @param number The string to parse
     * @return The String parsed to int, or -1 if invalid.
     */
    private static int parseIntSafely(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
