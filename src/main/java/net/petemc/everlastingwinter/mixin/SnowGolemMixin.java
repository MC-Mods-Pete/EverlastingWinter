package net.petemc.everlastingwinter.mixin;

import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SnowGolemEntity.class)
public abstract class SnowGolemMixin {
	// Vanilla melts snow golems in biomes tagged snow_golem_melts (desert, savanna,
	// badlands, ...), no matter how cold the biome is. Listed biomes are made cold
	// enough to snow, so golems should not melt wherever it can snow.
	@Redirect(method = "tickMovement", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/registry/entry/RegistryEntry;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
	private boolean redirectMeltCheck(RegistryEntry<Biome> biome, TagKey<Biome> tag) {
		if (tag == BiomeTags.SNOW_GOLEM_MELTS) {
			SnowGolemEntity golem = (SnowGolemEntity)(Object)this;
			if (biome.value().isCold(golem.getBlockPos())) {
				return false;
			}
		}
		return biome.isIn(tag);
	}
}
