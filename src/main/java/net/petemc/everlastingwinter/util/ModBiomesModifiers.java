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
