package net.petemc.everlastingwinter.mixin;

import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SnowGolem.class)
public abstract class SnowGolemMixin {
	// Vanilla melts snow golems in biomes tagged snow_golem_melts (desert, savanna,
	// badlands, ...), no matter how cold the biome is. Listed biomes are made cold
	// enough to snow, so golems should not melt wherever it can snow.
	@Redirect(method = "aiStep", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/tags/TagKey;)Z"))
	private boolean redirectMeltCheck(Holder<Biome> biome, TagKey<Biome> tag) {
		if (tag == BiomeTags.SNOW_GOLEM_MELTS) {
			SnowGolem golem = (SnowGolem)(Object)this;
			if (biome.value().coldEnoughToSnow(golem.blockPosition())) {
				return false;
			}
		}
		return biome.is(tag);
	}
}
