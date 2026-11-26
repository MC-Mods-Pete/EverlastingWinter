package net.petemc.everlastingwinter.mixin;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.petemc.everlastingwinter.config.MainConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobMixin {
	// Vanilla only protects undead mobs from sunlight in rain, not in snowfall.
	// While the endless snowfall is on, treat open sky in a biome with
	// precipitation as protected, so zombies and skeletons don't burn.
	@Inject(method = "isAffectedByDaylight", at = @At("HEAD"), cancellable = true)
	private void onIsAffectedByDaylight(CallbackInfoReturnable<Boolean> cir) {
		if (MainConfig.isConstantSnowfall()) {
			MobEntity mob = (MobEntity)(Object)this;
			World world = mob.getWorld();
			BlockPos pos = BlockPos.ofFloored(mob.getX(), mob.getEyeY(), mob.getZ());
			if (world.isSkyVisible(pos) && world.getBiome(pos).value().hasPrecipitation()) {
				cir.setReturnValue(false);
			}
		}
	}
}
