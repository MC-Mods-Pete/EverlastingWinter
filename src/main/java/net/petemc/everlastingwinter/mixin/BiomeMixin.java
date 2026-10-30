package net.petemc.everlastingwinter.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.petemc.everlastingwinter.config.MainConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class BiomeMixin {

	@Inject(method = "hasPrecipitation", at = @At("RETURN"), cancellable = true)
	private void ewo$hasPrecipitation(CallbackInfoReturnable<Boolean> cir) {
		if (isConfiguredSnowBiome()) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "coldEnoughToSnow", at = @At("RETURN"), cancellable = true)
	private void ewo$coldEnoughToSnow(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (isConfiguredSnowBiome()) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "getPrecipitationAt", at = @At("RETURN"), cancellable = true)
	private void ewo$getPrecipitationAt(BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir) {
		if (isConfiguredSnowBiome()) {
			cir.setReturnValue(Biome.Precipitation.SNOW);
		}
	}

	private boolean isConfiguredSnowBiome() {
		Registry<?> rawBiomeRegistry = BuiltInRegistries.REGISTRY.get(Registries.BIOME.location());
		if (rawBiomeRegistry == null) {
			return false;
		}
		@SuppressWarnings("unchecked")
		Registry<Biome> biomeRegistry = (Registry<Biome>) rawBiomeRegistry;
		ResourceLocation biomeId = biomeRegistry.getKey((Biome) (Object) this);
		return MainConfig.isSnowBiome(biomeId);
	}
}
