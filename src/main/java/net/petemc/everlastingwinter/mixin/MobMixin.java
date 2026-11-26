package net.petemc.everlastingwinter.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.petemc.everlastingwinter.config.MainConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {
	// Vanilla only protects undead mobs from sunlight in rain, not in snowfall.
	// While the endless snowfall is on, treat open sky in a biome with
	// precipitation as protected, so zombies and skeletons don't burn.
	@Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
	private void onIsSunBurnTick(CallbackInfoReturnable<Boolean> cir) {
		if (MainConfig.isConstantSnowfall()) {
			Mob mob = (Mob)(Object)this;
			Level level = mob.level();
			BlockPos pos = BlockPos.containing(mob.getX(), mob.getEyeY(), mob.getZ());
			if (level.canSeeSky(pos) && level.getBiome(pos).value().hasPrecipitation()) {
				cir.setReturnValue(false);
			}
		}
	}
}
