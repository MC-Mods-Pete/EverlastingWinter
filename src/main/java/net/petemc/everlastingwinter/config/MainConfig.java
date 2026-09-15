package net.petemc.everlastingwinter.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.petemc.everlastingwinter.EverlastingWinter;

@Config(name = EverlastingWinter.MOD_ID)
public class MainConfig implements ConfigData {

    public static boolean isConstantSnowfall() {
        Boolean o = RuntimeOverrides.getBoolean("constantSnowfall");
        return o != null ? o : INSTANCE.constantSnowfall;
    }

    public static boolean isReplaceSmallMushrooms() {
        Boolean o = RuntimeOverrides.getBoolean("replaceSmallMushrooms");
        return o != null ? o : INSTANCE.replaceSmallMushrooms;
    }

    public static boolean isReplaceTallFlowers() {
        Boolean o = RuntimeOverrides.getBoolean("replaceTallFlowers");
        return o != null ? o : INSTANCE.replaceTallFlowers;
    }

    public static int getLayerDepth() {
        Integer o = RuntimeOverrides.getInt("layerDepth");
        return o != null ? Math.max(1, o) : INSTANCE.layerDepth;
    }

    public static int getSnowTickChance() {
        Integer o = RuntimeOverrides.getInt("snowTickChance");
        return o != null ? o : INSTANCE.snowTickChance;
    }

    public static boolean isEnablePowderSnow() {
        Boolean o = RuntimeOverrides.getBoolean("enablePowderSnow");
        return o != null ? o : INSTANCE.enablePowderSnow;
    }

    public static int getPowderSnowChance() {
        Integer o = RuntimeOverrides.getInt("powderSnowChance");
        return o != null ? Math.max(0, Math.min(100, o)) : INSTANCE.powderSnowChance;
    }

    public static boolean isShouldReplaceFlowersAndGrass() {
        Boolean o = RuntimeOverrides.getBoolean("shouldReplaceFlowersAndGrass");
        return o != null ? o : INSTANCE.shouldReplaceFlowersAndGrass;
    }

    public static boolean isBreakCropsAndFarmland() {
        Boolean o = RuntimeOverrides.getBoolean("breakCropsAndFarmland");
        return o != null ? o : INSTANCE.breakCropsAndFarmland;
    }

    public static boolean isReplacePaths() {
        Boolean o = RuntimeOverrides.getBoolean("replacePaths");
        return o != null ? o : INSTANCE.replacePaths;
    }

    public static boolean isReplaceTorches() {
        Boolean o = RuntimeOverrides.getBoolean("replaceTorches");
        return o != null ? o : INSTANCE.replaceTorches;
    }

    public static float getBiomeTemperature() {
        return INSTANCE.biomeTemperature;
    }

    static boolean rawConstantSnowfall() {
        return INSTANCE.constantSnowfall;
    }

    static int rawLayerDepth() {
        return INSTANCE.layerDepth;
    }

    static int rawSnowTickChance() {
        return INSTANCE.snowTickChance;
    }

    static boolean rawShouldReplaceFlowersAndGrass() {
        return INSTANCE.shouldReplaceFlowersAndGrass;
    }

    static boolean rawReplaceSmallMushrooms() {
        return INSTANCE.replaceSmallMushrooms;
    }

    static boolean rawReplaceTallFlowers() {
        return INSTANCE.replaceTallFlowers;
    }

    static boolean rawBreakCropsAndFarmland() {
        return INSTANCE.breakCropsAndFarmland;
    }

    static boolean rawReplacePaths() {
        return INSTANCE.replacePaths;
    }

    static boolean rawReplaceTorches() {
        return INSTANCE.replaceTorches;
    }

    static boolean rawEnablePowderSnow() {
        return INSTANCE.enablePowderSnow;
    }

    static int rawPowderSnowChance() {
        return INSTANCE.powderSnowChance;
    }



    public static String[] getListOfSnowBiomes() {
        return INSTANCE.listOfSnowBiomes;
    }

    @ConfigEntry.Gui.Excluded
    private static MainConfig INSTANCE;

    public static void load() {
        AutoConfig.register(MainConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(MainConfig.class).getConfig();
    }

    @ConfigEntry.Gui.Tooltip()
    @Comment("If true, it's always snowing (forces snowfall regardless of weather) | default: true")
    private boolean constantSnowfall = true;

    @ConfigEntry.Gui.Tooltip()
    @Comment("Maximum total snow height in eighth-blocks above non-snow blocks (1 layer = 1, 1 snow block = 8 layers). Snow blocks stack upward, existing blocks are never replaced | default: 12 | min: 1")
    private int layerDepth = 12;

    @ConfigEntry.Gui.Tooltip()
    @Comment("Chance (in percent) that a snow tick happens for a chunk each game tick | default: 3")
    private int snowTickChance = 3;

    @ConfigEntry.Gui.Tooltip()
    @Comment("If true, should replace flowers and grass with snow layers | default: true")
    private boolean shouldReplaceFlowersAndGrass = true;

    @ConfigEntry.Gui.Tooltip()
    @Comment("If true, small mushrooms growing on the ground can be replaced with snow layers | default: false")
    private boolean replaceSmallMushrooms = false;

    @ConfigEntry.Gui.Tooltip()
    @Comment("If true, tall flowers can be replaced with snow layers | default: true")
    private boolean replaceTallFlowers = true;

    @ConfigEntry.Gui.Tooltip()
    @Comment("The temperature of the biome | default: -1.0")
    private float biomeTemperature = -1.0f;

    @ConfigEntry.Gui.Tooltip()
    @Comment("If true, crops are removed and farmland is turned into dirt so snow can accumulate | default: true")
    private boolean breakCropsAndFarmland = true;

    @ConfigEntry.Gui.Tooltip()
    @Comment("If true, dirt paths are replaced with dirt so that snow can accumulate on them | default: true")
    private boolean replacePaths = true;

    @ConfigEntry.Gui.Tooltip()
    @Comment("If true, torches on the ground are removed (item drops) and replaced with snow layers | default: false")
    private boolean replaceTorches = false;

    @ConfigEntry.Gui.Tooltip()
    @Comment("If true, snow layers can turn into powder snow | default: false")
    private boolean enablePowderSnow = false;

    @ConfigEntry.Gui.Tooltip()
    @Comment("Chance (in percent) that a snow layer turns into powder snow | default: 10 | min: 0 | max: 100")
    private int powderSnowChance = 10;

    @ConfigEntry.Gui.Tooltip()
    @Comment("A list with all the biomes that should get snow layers")
    private String[] listOfSnowBiomes = new String[]{
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
            "minecraft:wooded_badlands"
            };
}

/*
public class MainConfig {
    private static Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    public static MainConfig INSTANCE;
    private int layer_depth = 3;
    private int layered_frequency = 32;
    private boolean replace_flowers_grass = true;

    public MainConfig() {
        INSTANCE = this;
    }

    public int getLayerDepth() {
        return this.layer_depth > 8 ? 8 : this.layer_depth;
    }

    public int getSnowTickChance() {
        return INSTANCE.snowTickChance;
    }

    public boolean shouldReplaceFlowersGrass() {
        return this.replace_flowers_grass;
    }

    public static String[] getSpawningList() { return INSTANCE.spawningList; }

    //@ConfigEntry.Gui.Tooltip()
    //@Comment("A list with all the mobs that can spawn from a broken pot [id:entity-probability] (probability is written in decimal. 1.0 = 100%, 0.5 = 50%, 0.03 = 3%)")
    private final String[] spawningList = new String[]{
            "minecraft:bat-0.03",
            "minecraft:endermite-0.06",
            "minecraft:slime-0.05",
            "minecraft:cave_spider-0.03",
            "minecraft:husk-0.05",
            "minecraft:vex-0.008",
            "minecraft:cat-0.005",
            "minecraft:silverfish-0.08" };

    public static void load() {
        File file = new File("config/layered_snowfall.json");
        if (!file.exists()) {
            generate();
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String s = null;

            while((s = reader.readLine()) != null) {
                sb.append(s);
            }

            reader.close();
            INSTANCE = (MainConfig)GSON.fromJson(sb.toString(), MainConfig.class);
        } catch (Exception var4) {
            INSTANCE = new MainConfig();
        }

        save();
    }

    private static void generate() {
        File file = new File("config/layered_snowfall.json");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        INSTANCE = new MainConfig();

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(GSON.toJson(INSTANCE));
            writer.close();
        } catch (Exception var2) {
        }

    }

    private static void save() {
        File file = new File("config/layered_snowfall.json");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(GSON.toJson(INSTANCE));
            writer.close();
        } catch (Exception var2) {
        }

    }
}

 */

