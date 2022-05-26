package wbs.trails.trails.data;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import wbs.utils.util.particles.data.ItemStackProvider;

import java.util.Collections;

public class ItemStackProducer extends MaterialDataProducer<ItemStack, ItemStackProducer> {
    public static ItemStackProducer deserialize(ConfigurationSection section, String path) {
        ItemStackProducer created = new ItemStackProducer();

        return MaterialDataProducer.configure(created, section, path);
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
    public String getUsage() {
        return "<item>";
    }

    @Override
    protected boolean isValid(Material check) {
        return check.isItem();
    }

    @Override
    protected String getInvalidMaterialString() {
        return "That is not a valid block.";
    }
}
