package net.petemc.everlastingwinter.mixin;

import net.minecraft.world.level.Level;
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

	// isRainingAt() is intentionally not overridden: in snowy biomes vanilla returns
	// false there, so snow golems, endermen and other water-sensitive mobs don't take
	// rain damage. Undead sunlight protection is handled in MobMixin instead.
}
