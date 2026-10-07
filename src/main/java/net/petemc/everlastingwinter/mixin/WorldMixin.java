package net.petemc.everlastingwinter.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.petemc.everlastingwinter.config.MainConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
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
	@Inject(method = "hasRain", at = @At("RETURN"), cancellable = true)
	private void onHasRain(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (MainConfig.isConstantSnowfall() && !cir.getReturnValue()) {
			// Only protect when sky is visible — same rule as real rain
			if (((World)(Object)this).isSkyVisible(pos)) {
				cir.setReturnValue(true);
			}
		}
	}
}
