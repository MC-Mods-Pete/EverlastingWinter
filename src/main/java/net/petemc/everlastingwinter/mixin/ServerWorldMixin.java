package net.petemc.everlastingwinter.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.DirtPathBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.KelpBlock;
import net.minecraft.block.KelpPlantBlock;
import net.minecraft.block.MushroomPlantBlock;
import net.minecraft.block.SeagrassBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.petemc.everlastingwinter.EverlastingWinter;
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
		chunkSnowTick(serverWorld, chunk);
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
	private static void chunkSnowTick(ServerWorld world, Chunk chunk) {
		ChunkPos chunkpos = chunk.getPos();
		int i = chunkpos.getStartX();
		int j = chunkpos.getStartZ();
		if (world.random.nextInt(100) < MainConfig.getSnowTickChance()) {
			BlockPos blockPos = findSnowTarget(world, world.getRandomPosInChunk(i, 0, j, 15));
			if (blockPos == null) {
				return;
			}
			Biome biome = world.getBiome(blockPos).value();
			BlockState targetState = world.getBlockState(blockPos);
			Block targetBlock = targetState.getBlock();
			if ((world.isRaining() || MainConfig.isConstantSnowfall()) && biome.isCold(blockPos)) {
				if (targetBlock == Blocks.POWDER_SNOW) {
					if (MainConfig.isEnablePowderSnow()) {
						BlockPos p = blockPos;
						while (world.getBlockState(p).isOf(Blocks.POWDER_SNOW)) {
							p = p.down();
						}
						if (world.getBlockState(p).isOf(Blocks.SNOW_BLOCK) && world.random.nextInt(100) < MainConfig.getPowderSnowChance()) {
							world.setBlockState(p, Blocks.POWDER_SNOW.getDefaultState());
						}
					}
					return;
				}
				if (!snowPossibleAtPosition(world, blockPos)) {
					return;
				}
				int layerDepth = Math.max(1, MainConfig.getLayerDepth());
				if (targetBlock == Blocks.SNOW) {
					int currentLayers = world.getBlockState(blockPos).get(SnowBlock.LAYERS);
					int totalHeight = currentLayers + countSnowBlocksBelow(world, blockPos) * 8;
					if (totalHeight < layerDepth) {
						if (currentLayers < 8) {
							world.setBlockState(blockPos, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, currentLayers + 1));
						} else {
							if (MainConfig.isEnablePowderSnow() && world.random.nextInt(100) < MainConfig.getPowderSnowChance()) {
								world.setBlockState(blockPos, Blocks.POWDER_SNOW.getDefaultState());
							} else {
								world.setBlockState(blockPos, Blocks.SNOW_BLOCK.getDefaultState());
								world.setBlockState(blockPos.up(), Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 1));
							}
						}
					}
				} else if (targetBlock != Blocks.SNOW_BLOCK && targetBlock != Blocks.POWDER_SNOW) {
					BlockState belowState = world.getBlockState(blockPos.down());
					if (belowState.isOf(Blocks.FARMLAND) && MainConfig.isBreakCropsAndFarmland()) {
						world.setBlockState(blockPos.down(), Blocks.DIRT.getDefaultState());
					} else if (belowState.isOf(Blocks.DIRT_PATH) && MainConfig.isReplacePaths()) {
						world.setBlockState(blockPos.down(), Blocks.DIRT.getDefaultState());
					}
					if (targetBlock instanceof TorchBlock && MainConfig.isReplaceTorches()) {
						Block.dropStacks(world.getBlockState(blockPos), world, blockPos);
					}
					// Remove the upper half of tall plants before placing snow
					if (targetBlock instanceof TallPlantBlock && MainConfig.isShouldReplaceFlowersAndGrass()) {
						DoubleBlockHalf half = targetState.get(TallPlantBlock.HALF);
						if (half == DoubleBlockHalf.LOWER) {
							world.setBlockState(blockPos.up(), Blocks.AIR.getDefaultState());
						} else {
							world.setBlockState(blockPos.down(), Blocks.AIR.getDefaultState());
						}
					}
					world.setBlockState(blockPos, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 1));
				}
			}
		}
	}

	@Unique
	private static BlockPos findSnowTarget(World world, BlockPos samplePos) {
		BlockPos motionBlocking = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, samplePos);
		BlockPos noLeaves = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, samplePos);

		// No tree overhead — use simple position
		if (motionBlocking.equals(noLeaves)) {
			return motionBlocking;
		}

		// Tree overhead: 50/50 whether snow lands on top of leaves or searches below
		if (world.getRandom().nextBoolean()) {
			return motionBlocking;
		}

		// Search downward from the no-leaves candidate for the ground under the canopy.
		// Special case for branchy trees (e.g. Acacia): when we encounter a log that has
		// air above it, give it a 10% chance to catch snow. Otherwise keep descending.
		BlockPos p = noLeaves;
		BlockState prev = world.getBlockState(p.up());
		for (int depth = 0; depth < 20 && p.getY() > world.getBottomY(); depth++) {
			BlockState cur = world.getBlockState(p);

			if (cur.isIn(BlockTags.LOGS)) {
				// 10% chance to place snow on this branch, but only if air is above it
				if (prev.isAir() && world.getRandom().nextInt(100) < 10) {
					return p.up();
				}
			} else if (!cur.isIn(BlockTags.LEAVES) && !cur.isAir()
					&& !cur.isOf(Blocks.POWDER_SNOW)
					&& !cur.isReplaceable()
					&& !cur.isIn(BlockTags.SMALL_FLOWERS)
					&& !cur.isIn(BlockTags.TALL_FLOWERS)) {
				// Solid non-tree, non-powder-snow ground found — place snow on top
				return p.up();
			}

			prev = cur;
			p = p.down();
		}

		// Nothing found — fall back to the no-leaves candidate
		return noLeaves;
	}

	@Unique
	private static boolean isIce(Block block) {
		return block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.BLUE_ICE || block == Blocks.FROSTED_ICE;
	}

	@Unique
	private static int countSnowBlocksBelow(World world, BlockPos pos) {
		int count = 0;
		BlockPos p = pos.down();
		while (world.getBlockState(p).isOf(Blocks.SNOW_BLOCK)) {
			count++;
			p = p.down();
		}
		return count;
	}

	@Unique
	private static boolean snowPossibleAtPosition(World world, BlockPos blockPos) {
		BlockState belowState = world.getBlockState(blockPos.down());
		Block below = belowState.getBlock();
		Block target = world.getBlockState(blockPos).getBlock();
		if (isIce(below) || isIce(target)
				|| below instanceof FluidBlock || target instanceof FluidBlock
				|| (below instanceof DirtPathBlock && !MainConfig.isReplacePaths())
				|| below instanceof KelpBlock || below instanceof KelpPlantBlock
				|| below instanceof SeagrassBlock || below instanceof TallSeagrassBlock
				|| below instanceof StairsBlock
				|| below instanceof FenceBlock || below instanceof FenceGateBlock || below instanceof WallBlock
				|| (below instanceof SlabBlock && belowState.get(SlabBlock.TYPE) == SlabType.BOTTOM)) {
			return false;
		}
		if (blockPos.getY() >= 0 && blockPos.getY() < 256) {
			BlockState blockstate = world.getBlockState(blockPos);
			return (blockstate.getBlock() == Blocks.SNOW) ||
					((blockstate.isReplaceable() || blockstate.isIn(BlockTags.SMALL_FLOWERS)) && MainConfig.isShouldReplaceFlowersAndGrass()) ||
					(blockstate.getBlock() instanceof MushroomPlantBlock && MainConfig.isReplaceSmallMushrooms()) ||
					(blockstate.isIn(BlockTags.TALL_FLOWERS) && MainConfig.isReplaceTallFlowers()) ||
					((blockstate.getBlock() instanceof CropBlock || blockstate.getBlock() instanceof StemBlock || blockstate.getBlock() instanceof SweetBerryBushBlock) && belowState.isOf(Blocks.FARMLAND) && MainConfig.isBreakCropsAndFarmland()) ||
					(blockstate.getBlock() instanceof TorchBlock && MainConfig.isReplaceTorches());
		}
		return false;
	}
}
