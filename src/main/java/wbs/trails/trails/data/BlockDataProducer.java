package wbs.trails.trails.data;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class BlockDataProducer extends MaterialDataProducer<BlockData, BlockDataProducer> {
    public static BlockDataProducer deserialize(ConfigurationSection section, String path) {
        BlockDataProducer created = new BlockDataProducer();

        return MaterialDataProducer.configure(created, section, path);
    }

    @Override
    public @NotNull BlockData produce() {
        return Bukkit.createBlockData(material);
    }

    @Override
    public String getUsage() {
        return "<block>";
    }

    @Override
    public Class<BlockData> getDataClass() {
        return BlockData.class;
    }

    @Override
    protected boolean isValid(Material check) {
        return check.isBlock();
    }

    @Override
    protected String getInvalidMaterialString() {
        return "That is not a valid block.";
    }
}
