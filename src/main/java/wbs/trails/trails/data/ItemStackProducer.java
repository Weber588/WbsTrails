package wbs.trails.trails.data;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.trails.trails.options.ConfigOption;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.particles.data.ItemStackProvider;

import java.util.*;
import java.util.stream.Collectors;

public class ItemStackProducer extends DataProducer<ItemStack, ItemStackProducer> {
    public static ItemStackProducer deserialize(ConfigurationSection section, String path) {
        ItemStackProducer created = new ItemStackProducer();

        String materialString = section.getString(path + ".material");
        created.material = WbsEnums.materialFromString(materialString);

        return created;
    }

    private Material material = Material.SAND;

    @Override
    public int configure(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("This particle requires additional data.");
        }

        material = WbsEnums.materialFromString(args[0], Material.AIR);

        if (!material.isItem()) {
            throw new IllegalArgumentException("That is not a valid item.");
        }

        return 1;
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        section.set(path + ".material", material.name());
    }

    @Override
    public Collection<ConfigOption<ItemStackProducer, ?>> getDataOptions() {
        return new LinkedList<>();
    }

    @Override
    public Class<ItemStack> getDataClass() {
        return ItemStack.class;
    }

    @Override
    public @NotNull ItemStack produce() {
        return new ItemStackProvider(Collections.singletonList(material));
    }

    @Override
    @Nullable
    public List<String> handleTab(String[] args) {
        if (args.length == 1) {
            return Arrays.stream(Material.values())
                    .filter(Material::isItem)
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        }

        return null;
    }

    @Override
    public String getUsage() {
        return "<block>";
    }
}
