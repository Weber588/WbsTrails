package wbs.trails.trails.options;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.configuration.ConfigurationSection;
import wbs.trails.WbsTrails;
import wbs.trails.trails.CustomRegisteredTrail;
import wbs.trails.trails.RegisteredTrail;
import wbs.trails.trails.TrailManager;
import wbs.utils.exceptions.InvalidConfigurationException;
import wbs.utils.util.plugin.WbsSettings;
import wbs.utils.util.providers.generator.num.DoubleGenerator;

public class TrailOptionProvider extends DoubleGenerator {
    private double value;
    private DoubleOption<?> option;
    private final String name;

    public TrailOptionProvider(TrailOptionProvider copy) {
        this.value = copy.value;
        this.option = copy.option;
        this.name = copy.name;

        CustomRegisteredTrail.registerToNewTrail(this);
    }

    public TrailOptionProvider(ConfigurationSection section, WbsSettings settings, String directory) {
        name = section.getString("name");

        if (name == null) {
            settings.logError("Name is a required field.", directory + "/name");
            throw new InvalidConfigurationException();
        }

        ConfigurationSection current = section;
        ConfigurationSection previous = null;

        while (current.getParent() != null) {
            previous = current;
            current = current.getParent();
        }

        String id;
        if (previous == null) {
            id = current.getName();
        } else {
            id = previous.getName();
        }

        RegisteredTrail<?> registration = TrailManager.getRegisteredTrail(id);

        if (registration == null) {
            settings.logError("Internal exception; invalid id \"" + id + "\". Please report this issue.", "Internal error");
            throw new InvalidConfigurationException();
        }

        for (ConfigOption<?, ?> option : registration.getOptions()) {
            if (option.getName().equalsIgnoreCase(name)) {
                if (!DoubleOption.class.isAssignableFrom(option.getClass())) {
                    settings.logError("Option with name \"" + name + "\" is not a double option. Please report this error.", directory);
                    throw new InvalidConfigurationException();
                }

                this.option = (DoubleOption<?>) option;
            }
        }

        if (this.option == null) {
            settings.logError("Trail option not found: " + name, directory + "/name");
            throw new InvalidConfigurationException();
        }
    }

    @Override
    protected double getNewValue() {
        return value;
    }

    @Override
    public void writeToConfig(ConfigurationSection configurationSection, String s) {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public TrailOptionProvider clone() {
        return new TrailOptionProvider(this);
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }
}
