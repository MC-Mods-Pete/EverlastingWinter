package net.petemc.everlastingwinter.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ClimateSettingsBuilder;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.petemc.everlastingwinter.EverlastingWinter;
import net.petemc.everlastingwinter.config.MainConfig;

public class ModBiomesModifiers {

    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> MODIFIER_CODECS = DeferredRegister.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,
            EverlastingWinter.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<EverlastingWinterBiomeModifier>> EWO_MODIFIER_CODEC = MODIFIER_CODECS.register(
            "everlasting_winter",
            () -> EverlastingWinterBiomeModifier.CODEC);

    public static class EverlastingWinterBiomeModifier implements BiomeModifier {

        public static final MapCodec<EverlastingWinterBiomeModifier> CODEC =
                MapCodec.unit(EverlastingWinterBiomeModifier::new);

        @Override
        public void modify(Holder<Biome> holder, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase != Phase.MODIFY) {
                return;
            }
            holder.unwrapKey().ifPresent(key -> {
                boolean match = MainConfig.isSnowBiome(key.location());
                if (match) {
                    ClimateSettingsBuilder climate = builder.getClimateSettings();
                    climate.setTemperature(MainConfig.getBiomeTemperature());
                    climate.setTemperatureModifier(Biome.TemperatureModifier.NONE);
                    climate.setDownfall(0.5F);
                    climate.setHasPrecipitation(true);
                    builder.getSpecialEffects().foliageColorOverride(0xDFDFDF);
                }
            });
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return CODEC;
        }
    }
}
