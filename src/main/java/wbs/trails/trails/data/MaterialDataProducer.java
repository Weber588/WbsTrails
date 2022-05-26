package wbs.trails.trails.data;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wbs.trails.WbsTrails;
import wbs.trails.menus.build.BuildMenu;
import wbs.trails.menus.build.data.MaterialDataMenu;
import wbs.trails.trails.Trail;
import wbs.trails.trails.options.ConfigOption;
import wbs.utils.util.WbsEnums;
import wbs.utils.util.menus.WbsMenu;

import java.util.*;
import java.util.stream.Collectors;

public abstract class MaterialDataProducer<D, P extends DataProducer<D, P>> extends DataProducer<D, P> {
    public static <P extends MaterialDataProducer<?, P>> P configure(P producer, ConfigurationSection section, String path) {
        String materialString = section.getString(path + ".material");
        producer.material = WbsEnums.materialFromString(materialString);

        return producer;
    }

    protected Material material = Material.SAND;

    @Override
    public int configure(String[] args) throws IllegalArgumentException {
        if (args.length < 1) {
            throw new IllegalArgumentException("This particle requires additional data.");
        }

        material = WbsEnums.materialFromString(args[0], material);

        if (!isValidPrivate(material)) {
            throw new IllegalArgumentException(getInvalidMaterialString());
        }

        return 1;
    }

    private boolean isValidPrivate(Material check) {
        return !check.isAir() && isValid(check);
    }

    protected abstract boolean isValid(Material check);
    protected abstract String getInvalidMaterialString();

    @Override
    public void writeToConfig(ConfigurationSection section, String path) {
        section.set(path + ".material", material.name());
    }

    @Override
    public Collection<ConfigOption<P, ?>> getDataOptions() {
        return new LinkedList<>();
    }

    @Override
    @Nullable
    public List<String> handleTab(String[] args) {
        if (args.length == 1) {
            return getValidMaterials()
                    .stream()
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        }

        return new LinkedList<>();
    }

    @Override
    public <T extends Trail<T>> WbsMenu getMenu(BuildMenu lastPage, T trail, Player player) {
        return new MaterialDataMenu<>(WbsTrails.getInstance(), lastPage, this, trail, player);
    }

    public Collection<Material> getValidMaterials() {
        return Arrays.stream(Material.values())
                .filter(this::isValidPrivate)
                .sorted(Comparator.comparing(Material::name))
                .collect(Collectors.toList());
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
