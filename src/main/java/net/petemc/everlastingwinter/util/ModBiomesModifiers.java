package net.petemc.everlastingwinter.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ClimateSettingsBuilder;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.petemc.everlastingwinter.EverlastingWinter;
import net.petemc.everlastingwinter.config.MainConfig;

public class ModBiomesModifiers {

    public static final DeferredRegister<Codec<? extends BiomeModifier>> MODIFIER_CODECS = DeferredRegister.create(
            ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,
            EverlastingWinter.MOD_ID);

    public static final RegistryObject<Codec<? extends BiomeModifier>> EWO_MODIFIER_CODEC = MODIFIER_CODECS.register(
            "everlasting_winter",
            () -> EverlastingWinterBiomeModifier.CODEC);

    public static class EverlastingWinterBiomeModifier implements BiomeModifier {

        public static final Codec<EverlastingWinterBiomeModifier> CODEC =
                Codec.unit(EverlastingWinterBiomeModifier::new);

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
        public Codec<? extends BiomeModifier> codec() {
            return CODEC;
        }
    }
}
