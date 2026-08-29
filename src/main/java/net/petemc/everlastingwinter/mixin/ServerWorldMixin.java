package net.petemc.everlastingwinter.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtPathBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.KelpBlock;
import net.minecraft.block.KelpPlantBlock;
import net.minecraft.block.MushroomPlantBlock;
import net.minecraft.block.SeagrassBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.petemc.everlastingwinter.config.MainConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
	@Shadow
	public abstract void setWeather(int clearDuration, int rainDuration, boolean raining, boolean thundering);

	@Unique
    private int counter = 0;

	@Unique
    private boolean lastMainConfigValue = false;

	@Inject(at = {@At("HEAD")}, method = {"tickChunk"})
	private void tickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo info) {
		ServerWorld serverWorld = (ServerWorld)(Object)this;
		chunkSnowTick(serverWorld, chunk, randomTickSpeed);
		if (lastMainConfigValue != MainConfig.isConstantSnowfall()) {
			lastMainConfigValue = MainConfig.isConstantSnowfall();
			if (!lastMainConfigValue) {
				this.setWeather((ServerWorld.CLEAR_WEATHER_DURATION_PROVIDER).get(((World) serverWorld).getRandom()), 0, false, false);
				counter = 0;
			}
		}
		if (counter == 6000) {
			this.setWeather(0, 7000, MainConfig.isConstantSnowfall(), false);
			counter = 0;
		} else {
			counter++;
		}
	}

	@Unique
	private static void chunkSnowTick(ServerWorld world, Chunk chunk, int randomTickSpeed) {
		ChunkPos chunkpos = chunk.getPos();
		int i = chunkpos.getStartX();
		int j = chunkpos.getStartZ();
		Random random = world.random;
		if (world.random.nextInt(100) < MainConfig.getSnowTickChance()) {

			BlockPos blockPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, world.getRandomPosInChunk(i, 0, j, 15));
			if (random.nextBoolean()) {
				blockPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, world.getRandomPosInChunk(i, 0, j, 15));
			}
			Biome biome = world.getBiome(blockPos).value();
			if ((world.isRaining() || MainConfig.isConstantSnowfall()) && biome.isCold(blockPos) && snowPossibleAtPosition(world, blockPos)) {
				int currentLayers = 0;
				if (world.getBlockState(blockPos).getBlock() == Blocks.SNOW) {
					currentLayers = world.getBlockState(blockPos).get(SnowBlock.LAYERS);
				}
				if (currentLayers < MainConfig.getLayerDepth()) {
					world.setBlockState(blockPos, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, currentLayers + 1));
				}
			}
		}
	}

	@Unique
	private static boolean isIce(Block block) {
		return block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.BLUE_ICE || block == Blocks.FROSTED_ICE;
	}

	@Unique
	private static boolean snowPossibleAtPosition(World world, BlockPos blockPos) {
		Block below = world.getBlockState(blockPos.down()).getBlock();
		Block target = world.getBlockState(blockPos).getBlock();
		if (isIce(below) || isIce(target)
				|| below instanceof FluidBlock || target instanceof FluidBlock
				|| below instanceof DirtPathBlock
				|| below instanceof KelpBlock || below instanceof KelpPlantBlock
				|| below instanceof SeagrassBlock || below instanceof TallSeagrassBlock) {
			return false;
		}
		if (blockPos.getY() >= 0 && blockPos.getY() < 256 && world.getLightLevel(LightType.BLOCK, blockPos) < 10) {
			BlockState blockstate = world.getBlockState(blockPos);
			return (blockstate.getBlock() == Blocks.SNOW) ||
					((blockstate.isReplaceable() || blockstate.isIn(BlockTags.SMALL_FLOWERS)) && MainConfig.isShouldReplaceFlowersAndGrass()) ||
					(blockstate.getBlock() instanceof MushroomPlantBlock && MainConfig.isReplaceSmallMushrooms()) ||
					(blockstate.isIn(BlockTags.TALL_FLOWERS) && MainConfig.isReplaceTallFlowers());
		}
		return false;
	}
}

