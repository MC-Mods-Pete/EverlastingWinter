package net.petemc.everlastingwinter.util;

import java.util.Arrays;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.petemc.everlastingwinter.EverlastingWinter;
import net.petemc.everlastingwinter.config.MainConfig;

public class ModBiomesModifiers {
    public static void load() {
        String[] snowBiomes = MainConfig.getListOfSnowBiomes();
        BiomeModifications.create(new Identifier(EverlastingWinter.MOD_ID, "biome_modifications"))
                .add(ModificationPhase.POST_PROCESSING,
                        BiomeSelectors.foundInOverworld().and(context ->
                                Arrays.stream(snowBiomes).anyMatch(s -> s.equalsIgnoreCase(context.getBiomeKey().getValue().toString()))),
                        (selection, context) -> {
                            context.getWeather().setPrecipitation(true);
                            context.getWeather().setTemperature(MainConfig.getBiomeTemperature());
                            context.getWeather().setTemperatureModifier(Biome.TemperatureModifier.NONE);
                            context.getEffects().setFoliageColor(0xdfdfdf);
                        });
    }

}

// Don't touch this class, it is a work in progress and will be used in the future to add more biome modifications.
/*
public class BiomesModifier extends BiomeModificationImpl {

                            //context.getWeather().setDownfall(1.0f);
                            //context.getEffects().setFogColor(0xdfdfdf);
                            //context.getEffects().
                            context.getEffects().setFoliageColor(0xdfdfdf);
                            //context.getEffects().setFoliageColor(0x86a28d);
                            //context.getEffects().setFoliageColor(0xb0cdb7);
                            //context.getEffects().setFoliageColor(0xc8dccd);

    private BiomesModifier() {
        super();
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.MODIFY) {
            ResourceLocation registryName = biome.unwrapKey().get().location();
            if (EWConfig.GENERAL.listMode.get() == ListMode.BLACKLIST) {
                if (EWConfig.GENERAL.biomeList.get().stream().anyMatch(s -> s.equals(registryName.getPath()) || s.equals(registryName.toString())))
                    return;
            } else if (EWConfig.GENERAL.listMode.get() == ListMode.WHITELIST) {
                if (EWConfig.GENERAL.biomeList.get().stream().noneMatch(s -> s.equals(registryName.getPath()) || s.equals(registryName.toString())))
                    return;
            } else {
                throw new RuntimeException("Wrong list mode! " + EWConfig.GENERAL.biomeList.get());
            }

            float downfall = EWConfig.GENERAL.downfall.get().floatValue();
            if (downfall != -1 && downfall < 0) {
                EternalWinter.LOGGER.error("Invalid downfall " + downfall + ", defaulting to -1!");
                downfall = -1F;
            }
            if (downfall != -1F)
                builder.getClimateSettings().setDownfall(downfall);
            builder.getClimateSettings().setTemperature(0F);
            builder.getClimateSettings().setTemperatureModifier(Biome.TemperatureModifier.NONE);
            builder.getClimateSettings().setHasPrecipitation(true);
            EternalWinter.LOGGER.debug("Modified Biome {} successful!", registryName);
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return EternalWinter.BIOME_MODIFIER_CODEC.get();
    }
}

 */
