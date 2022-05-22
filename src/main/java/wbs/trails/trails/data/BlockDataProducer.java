package wbs.trails.trails.data;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wbs.trails.trails.options.ConfigOption;
import wbs.utils.util.WbsEnums;

import java.util.*;
import java.util.stream.Collectors;

public class BlockDataProducer extends DataProducer<BlockData, BlockDataProducer> {
    public static BlockDataProducer deserialize(ConfigurationSection section, String path) {
        BlockDataProducer created = new BlockDataProducer();

        String materialString = section.getString(path + ".material");
        created.material = WbsEnums.materialFromString(materialString);

        return created;
    }

    // TODO: Allow multiple materials to be listed?
    //  Build intermediate object
    private Material material;

    @Override
    public int configure(String[] args) throws IllegalArgumentException {
        if (args.length < 1) {
            throw new IllegalArgumentException("This particle requires additional data.");
        }

        material = WbsEnums.materialFromString(args[0], Material.AIR);

        if (!material.isBlock()) {
            throw new IllegalArgumentException("That is not a valid block.");
        }

        return 1;
    }

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        section.set(path + ".material", material.name());
    }

    @Override
    public Collection<ConfigOption<BlockDataProducer, ?>> getDataOptions() {
        return new LinkedList<>();
    }

    @Override
    public @NotNull BlockData produce() {
        return Bukkit.createBlockData(material);
    }

    @Override
    @Nullable
    public List<String> handleTab(String[] args) {
        if (args.length == 1) {
            return Arrays.stream(Material.values())
                    .filter(Material::isBlock)
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

    @Override
    public Class<BlockData> getDataClass() {
        return BlockData.class;
    }
}
