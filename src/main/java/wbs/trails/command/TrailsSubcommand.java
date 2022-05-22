package wbs.trails.command;

import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wbs.trails.trails.*;
import wbs.trails.TrailsController;
import wbs.trails.TrailsSettings;
import wbs.trails.WbsTrails;
import wbs.trails.trails.data.DataManager;
import wbs.trails.trails.data.DataProducer;
import wbs.trails.trails.options.ConfigOption;
import wbs.trails.trails.options.OptionPair;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.commands.WbsSubcommand;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TrailsSubcommand extends WbsSubcommand {

    protected final WbsTrails plugin;
    protected final TrailsSettings settings;
    protected final TrailsController controller;

    public TrailsSubcommand(@NotNull WbsTrails plugin, @NotNull String label) {
        super(plugin, label);
        this.plugin = plugin;
        settings = plugin.settings;
        controller = TrailsController.getInstance();
    }

    protected Trail<?> buildTrail(Player player, String[] args) {
        Particle particle;
        if (args.length < 3) {
            sendMessage("Usage: &h/trails " + args[0] + " <type> <particle>", player);
            return null;
        }

        particle = WbsEnums.particleFromString(args[2]);

        if (particle == null) {
            sendMessage("&h" + args[2] + " &wis not a valid particle.", player);
            return null;
        }

        if (settings.getParticleBlacklist().contains(particle)) {
            sendMessage("&wThat particle is blacklisted.", player);
            return null;
        }

        if (!settings.getAllowedParticlesFor(player).contains(particle)) {
            sendMessage("&wYou do not have permission to use the particle &h" + args[2] + "&w.", player);
            return null;
        }

        RegisteredTrail<?> registration = TrailManager.getRegisteredTrail(args[1]);

        if (registration == null) {
            sendMessage("Invalid type. Please choose from the following: " + TrailManager.getTrailNames(), player);
            return null;
        }

        if (!player.hasPermission("wbstrails.type." + registration.getName().toLowerCase())) {
            sendMessage("You do not have access to that trail type.", player);
            return null;
        }

        Class<?> clazz = particle.getDataType();

        int optionsStart = 3;

        DataProducer<?, ?> producer = null;
        List<ConfigOption<?, ?>> dataOptions = new LinkedList<>();

        if (clazz != Void.class) {
            String[] dataArgs = Arrays.copyOfRange(args, 3, args.length);

            producer = DataManager.getProducer(clazz);

            if (producer == null) {
                sendMessage("&wA data type was missing &h(" + clazz.getCanonicalName() + ")", player);
                return null;
            }

            int argsUsed;
            try {
                argsUsed = producer.configure(dataArgs);
            } catch (IllegalArgumentException e) {
                sendMessage("&w" + e.getMessage(), player);
                return null;
            }

            optionsStart += argsUsed;
            dataOptions.addAll(producer.getDataOptions());
        }

        Trail<?> trail = registration.buildTrail(player);
        trail.setParticle(particle);

        for (int i = optionsStart; i < args.length-1; i+=2) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            boolean succeeded = trySetOption((Trail) trail, producer, dataOptions, args[i], args[i + 1], player);
            if (!succeeded) {
                return null;
            }
        }

        if (producer != null) {
            trail.setData(producer);
        }

        return trail;
    }

    /**
     * @return Whether or not the setting succeeded
     */
    private <T extends Trail<T>> boolean trySetOption(T trail, DataProducer<?, ?> producer, List<ConfigOption<?, ?>> dataOptions, String optionName, String valueOption, Player player) {
        RegisteredTrail<T> registration = trail.getRegistration();
        ConfigOption<T, ?> option = registration.getOption(optionName);

        if (option == null) {
            for (ConfigOption<?, ?> check : dataOptions) {
                if (check.getName().equalsIgnoreCase(optionName)) {
                    // dataOptions always accept the provided producer, but can't resolve the generics of it
                    //noinspection unchecked
                    ConfigOption<DataProducer<?, ?>, ?> dataOption = (ConfigOption<DataProducer<?, ?>, ?>) check;

                    OptionPair<DataProducer<?, ?>, ?> pair = dataOption.pairFromValue(valueOption);
                    if (pair == null) {
                        sendMessage(dataOption.getPrompt() + " &h(" + optionName + " " + valueOption + ")", player);
                        return false;
                    }

                    pair.apply(producer);
                    return true;
                }
            }
        }

        if (option == null) {
            sendMessage("&h" + optionName + "&w is not a valid option for this trail."
                    + " Please choose from the following: &h" + String.join(", ", registration.getOptionNames()) + " &h(" + optionName + " " + valueOption + ")", player);
            return false;
        }

        OptionPair<T, ?> pair = option.pairFromValue(valueOption);
        if (pair == null) {
            sendMessage(option.getPrompt() + " &h(" + optionName + " " + valueOption + ")", player);
            return false;
        }

        pair.apply(trail);
        return true;
    }

    protected List<String> getTrailTabCompletions(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        List<String> choices = new LinkedList<>();

        Particle particle;

        switch (args.length) {
            case 2:
                for (String typeString : TrailManager.getTrailNames()) {
                    if (sender.hasPermission("wbstrails.type." + typeString)) {
                        choices.add(typeString);
                    }
                }
                break;
            case 3:
                if (sender instanceof Player) {
                    for (Particle allowedParticle : settings.getAllowedParticlesFor((Player) sender)) {
                        choices.add(allowedParticle.name().toLowerCase());
                    }
                }
                break;
        }

        if (args.length > 3) {
            int optionsStart = 3;
            Collection<? extends ConfigOption<?, ?>> dataOptions = null;

            particle = WbsEnums.particleFromString(args[2]);
            if (particle != null) {
                Class<?> clazz = particle.getDataType();

                if (clazz != Void.class) {
                    String[] dataArgs = Arrays.copyOfRange(args, optionsStart, args.length);

                    List<String> dataSuggestions = DataManager.handleTab(clazz, dataArgs);

                    if (dataSuggestions != null) {
                        return dataSuggestions;
                    } else { // If no suggestions, then no more args are needed, and the data should build properly
                        DataProducer<?, ?> producer = DataManager.getProducer(clazz);

                        if (producer == null) return choices;

                        int argsUsed;
                        try {
                            argsUsed = producer.configure(dataArgs);
                        } catch (IllegalArgumentException e) {
                            return choices;
                        }

                        optionsStart += argsUsed;

                        dataOptions = producer.getDataOptions();

                        if (args.length < optionsStart) {
                            return choices;
                        }
                    }
                }
            }

            RegisteredTrail<?> registration = TrailManager.getRegisteredTrail(args[1]);
            if (registration == null) {
                return choices;
            }

            if ((args.length - optionsStart) % 2 == 1) {
                List<String> editable = new LinkedList<>(registration.getOptionNames());

                if (dataOptions != null) {
                    dataOptions.forEach(option -> editable.add(option.getName()));
                }

                for (String arg : args) {
                    editable.remove(arg);
                }
                choices.addAll(editable);
            } else {
                String optionName = args[args.length - 2];

                ConfigOption<?, ?> option = registration.getOption(optionName);

                if (dataOptions != null && option == null) {
                    for (ConfigOption<?, ?> check : dataOptions) {
                        if (check.getName().equalsIgnoreCase(optionName)) {
                            option = check;
                            break;
                        }
                    }
                }

                if (option != null) {
                    option.getAutoCompletions().stream()
                            .map(Object::toString)
                            .forEach(choices::add);
                }
            }
        }

        return choices;
    }
}
