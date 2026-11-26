package net.petemc.everlastingwinter.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SnowGolem.class)
public abstract class SnowGolemMixin {
	// Vanilla melts snow golems in biomes with the snow_golem_melts attribute
	// (desert, savanna, badlands, ...), no matter how cold the biome is. Listed
	// biomes are made cold enough to snow, so golems should not melt wherever it
	// can snow.
	@Redirect(method = "aiStep", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem;getValue(Lnet/minecraft/world/attribute/EnvironmentAttribute;Lnet/minecraft/world/phys/Vec3;)Ljava/lang/Object;"))
	private Object redirectMeltCheck(EnvironmentAttributeSystem system, EnvironmentAttribute<?> attribute, Vec3 position) {
		Object value = system.getValue(attribute, position);
		if (attribute == EnvironmentAttributes.SNOW_GOLEM_MELTS && Boolean.TRUE.equals(value)) {
			SnowGolem golem = (SnowGolem)(Object)this;
			Level level = golem.level();
			BlockPos pos = golem.blockPosition();
			if (level.getBiome(pos).value().coldEnoughToSnow(pos, level.getSeaLevel())) {
				return Boolean.FALSE;
			}
		}
		return value;
	}
}
