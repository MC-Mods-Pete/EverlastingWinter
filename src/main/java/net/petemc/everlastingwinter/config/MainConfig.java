package net.petemc.everlastingwinter.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.serialization.Dynamic;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import java.util.Arrays;
import java.util.List;

public class MainConfig {
    private static final List<String> DEFAULT_SNOW_BIOMES = Arrays.asList(
            "minecraft:stony_shore",
            "minecraft:windswept_forest",
            "minecraft:windswept_hills",
            "minecraft:windswept_gravelly_hills",
            "minecraft:old_growth_pine_taiga",
            "minecraft:old_growth_spruce_taiga",
            "minecraft:taiga",
            "minecraft:cherry_grove",
            "minecraft:meadow",
            "minecraft:cold_ocean",
            "minecraft:deep_cold_ocean",
            "minecraft:deep_ocean",
            "minecraft:ocean",
            "minecraft:river",
            "minecraft:birch_forest",
            "minecraft:old_growth_birch_forest",
            "minecraft:dark_forest",
            "minecraft:flower_forest",
            "minecraft:forest",
            "minecraft:pale_garden",
            "minecraft:beach",
            "minecraft:mangrove_swamp",
            "minecraft:plains",
            "minecraft:sunflower_plains",
            "minecraft:swamp",
            "minecraft:lukewarm_ocean",
            "minecraft:deep_lukewarm_ocean",
            "minecraft:warm_ocean",
            "minecraft:mushroom_fields",
            "minecraft:bamboo_jungle",
            "minecraft:jungle",
            "minecraft:sparse_jungle",
            "minecraft:stony_peaks",
            "minecraft:windswept_savanna",
            "minecraft:savanna",
            "minecraft:savanna_plateau",
            "minecraft:badlands",
            "minecraft:desert",
            "minecraft:eroded_badlands",
            "minecraft:wooded_badlands");

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.ConfigValue<Boolean> CONSTANT_SNOWFALL;
    private static final ForgeConfigSpec.ConfigValue<Integer> LAYER_DEPTH;
    private static final ForgeConfigSpec.ConfigValue<Integer> SNOW_TICK_CHANCE;
    private static final ForgeConfigSpec.ConfigValue<Boolean> SHOULD_REPLACE_FLOWERS_AND_GRASS;
    private static final ForgeConfigSpec.ConfigValue<Boolean> REPLACE_SMALL_MUSHROOMS;
    private static final ForgeConfigSpec.ConfigValue<Boolean> REPLACE_TALL_FLOWERS;
    private static final ForgeConfigSpec.ConfigValue<Double> BIOME_TEMPERATURE;
    private static final ForgeConfigSpec.ConfigValue<Boolean> BREAK_CROPS_AND_FARMLAND;
    private static final ForgeConfigSpec.ConfigValue<Boolean> REPLACE_PATHS;
    private static final ForgeConfigSpec.ConfigValue<Boolean> REPLACE_TORCHES;
    private static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_POWDER_SNOW;
    private static final ForgeConfigSpec.ConfigValue<Integer> POWDER_SNOW_CHANCE;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> LIST_OF_SNOW_BIOMES;

    static {
        BUILDER.comment("Everlasting Winter configuration").push("general");

        CONSTANT_SNOWFALL = BUILDER
                .comment("If true, it's always snowing (forces snowfall regardless of weather) | default: true")
                .define("constantSnowfall", true);

        LAYER_DEPTH = BUILDER
                .comment("Maximum total snow height in eighth-blocks above non-snow blocks (1 layer = 1, 1 snow block = 8 layers). Snow blocks stack upward, existing blocks are never replaced | default: 12 | min: 1")
                .defineInRange("layerDepth", 12, 1, 255);

        SNOW_TICK_CHANCE = BUILDER
                .comment("Chance (in percent) that a snow tick happens for a chunk each game tick | default: 3")
                .defineInRange("snowTickChance", 3, 0, 100);

        SHOULD_REPLACE_FLOWERS_AND_GRASS = BUILDER
                .comment("If true, should replace flowers and grass with snow layers | default: true")
                .define("shouldReplaceFlowersAndGrass", true);

        REPLACE_SMALL_MUSHROOMS = BUILDER
                .comment("If true, small mushrooms growing on the ground can be replaced with snow layers | default: false")
                .define("replaceSmallMushrooms", false);

        REPLACE_TALL_FLOWERS = BUILDER
                .comment("If true, tall flowers can be replaced with snow layers | default: true")
                .define("replaceTallFlowers", true);

        BIOME_TEMPERATURE = BUILDER
                .comment("The temperature of the biome | default: -1.0")
                .defineInRange("biomeTemperature", -1.0, -2.0, 2.0);

        BREAK_CROPS_AND_FARMLAND = BUILDER
                .comment("If true, crops are removed and farmland is turned into dirt so snow can accumulate | default: true")
                .define("breakCropsAndFarmland", true);

        REPLACE_PATHS = BUILDER
                .comment("If true, dirt paths are replaced with dirt so that snow can accumulate on them | default: true")
                .define("replacePaths", true);

        REPLACE_TORCHES = BUILDER
                .comment("If true, torches on the ground are removed (item drops) and replaced with snow layers | default: false")
                .define("replaceTorches", false);

        ENABLE_POWDER_SNOW = BUILDER
                .comment("If true, snow layers can turn into powder snow | default: false")
                .define("enablePowderSnow", false);

        POWDER_SNOW_CHANCE = BUILDER
                .comment("Chance (in percent) that a snow layer turns into powder snow | default: 10 | min: 0 | max: 100")
                .defineInRange("powderSnowChance", 10, 0, 100);

        BUILDER.pop();

        BUILDER.push("biomes");

        LIST_OF_SNOW_BIOMES = BUILDER
                .comment("A list with all the biomes that should get snow layers")
                .defineListAllowEmpty("listOfSnowBiomes",
                        DEFAULT_SNOW_BIOMES,
                         o -> o instanceof String);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static boolean isConstantSnowfall() {
        Boolean o = RuntimeOverrides.getBoolean("constantSnowfall");
        return o != null ? o : CONSTANT_SNOWFALL.get();
    }

    public static boolean isReplaceSmallMushrooms() {
        Boolean o = RuntimeOverrides.getBoolean("replaceSmallMushrooms");
        return o != null ? o : REPLACE_SMALL_MUSHROOMS.get();
    }

    public static boolean isReplaceTallFlowers() {
        Boolean o = RuntimeOverrides.getBoolean("replaceTallFlowers");
        return o != null ? o : REPLACE_TALL_FLOWERS.get();
    }

    public static int getLayerDepth() {
        Integer o = RuntimeOverrides.getInt("layerDepth");
        return o != null ? Math.max(1, o) : LAYER_DEPTH.get();
    }

    public static int getSnowTickChance() {
        Integer o = RuntimeOverrides.getInt("snowTickChance");
        return o != null ? o : SNOW_TICK_CHANCE.get();
    }

    public static boolean isEnablePowderSnow() {
        Boolean o = RuntimeOverrides.getBoolean("enablePowderSnow");
        return o != null ? o : ENABLE_POWDER_SNOW.get();
    }

    public static int getPowderSnowChance() {
        Integer o = RuntimeOverrides.getInt("powderSnowChance");
        return o != null ? Math.max(0, Math.min(100, o)) : POWDER_SNOW_CHANCE.get();
    }

    public static boolean isShouldReplaceFlowersAndGrass() {
        Boolean o = RuntimeOverrides.getBoolean("shouldReplaceFlowersAndGrass");
        return o != null ? o : SHOULD_REPLACE_FLOWERS_AND_GRASS.get();
    }

    public static boolean isBreakCropsAndFarmland() {
        Boolean o = RuntimeOverrides.getBoolean("breakCropsAndFarmland");
        return o != null ? o : BREAK_CROPS_AND_FARMLAND.get();
    }

    public static boolean isReplacePaths() {
        Boolean o = RuntimeOverrides.getBoolean("replacePaths");
        return o != null ? o : REPLACE_PATHS.get();
    }

    public static boolean isReplaceTorches() {
        Boolean o = RuntimeOverrides.getBoolean("replaceTorches");
        return o != null ? o : REPLACE_TORCHES.get();
    }

    public static float getBiomeTemperature() {
        return BIOME_TEMPERATURE.get().floatValue();
    }

    public static List<? extends String> getListOfSnowBiomes() {
        return LIST_OF_SNOW_BIOMES.get();
    }

    public static boolean isSnowBiome(ResourceLocation biomeId) {
        if (biomeId == null) {
            return false;
        }
        for (String rawBiomeId : LIST_OF_SNOW_BIOMES.get()) {
            if (rawBiomeId == null) {
                continue;
            }
            ResourceLocation configured = ResourceLocation.tryParse(rawBiomeId.trim());
            if (configured != null && configured.equals(biomeId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean rawConstantSnowfall() {
        return true;
    }

    public static int rawLayerDepth() {
        return 12;
    }

    public static int rawSnowTickChance() {
        return 3;
    }

    public static boolean rawShouldReplaceFlowersAndGrass() {
        return true;
    }

    public static boolean rawReplaceSmallMushrooms() {
        return false;
    }

    public static boolean rawReplaceTallFlowers() {
        return true;
    }

    public static boolean rawBreakCropsAndFarmland() {
        return true;
    }

    public static boolean rawReplacePaths() {
        return true;
    }

    public static boolean rawReplaceTorches() {
        return false;
    }

    public static boolean rawEnablePowderSnow() {
        return false;
    }

    public static int rawPowderSnowChance() {
        return 10;
    }

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SPEC);
    }
}
