package net.petemc.everlastingwinter.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.petemc.everlastingwinter.config.MainConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class WorldMixin {
	@Inject(method = "isRaining", at = @At("RETURN"), cancellable = true)
	private void onIsRaining(CallbackInfoReturnable<Boolean> cir) {
		if (MainConfig.isConstantSnowfall()) {
			cir.setReturnValue(true);
		}
	}

	// hasRain() checks sky visibility and biome in addition to isRaining(),
	// which can still return false under tree canopies etc.
	// Override it directly so undead mobs are protected in all open areas.
	@Inject(method = "isRainingAt", at = @At("RETURN"), cancellable = true)
	private void onHasRain(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (MainConfig.isConstantSnowfall() && !cir.getReturnValue()) {
			Level world = (Level)(Object)this;
			// Only protect when sky is visible — same rule as real rain.
			// Skip biomes without precipitation (End, Nether, deserts, ...),
			// otherwise endermen there take rain damage and keep teleporting.
			if (world.canSeeSky(pos) && world.getBiome(pos).value().getPrecipitation() != Biome.Precipitation.NONE) {
				cir.setReturnValue(true);
			}
		}
	}
}
