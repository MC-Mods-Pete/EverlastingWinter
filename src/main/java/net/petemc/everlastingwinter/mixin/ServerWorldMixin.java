package net.petemc.everlastingwinter.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.petemc.everlastingwinter.config.MainConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin {
	@Shadow
	public abstract void setWeatherParameters(int clearDuration, int rainDuration, boolean raining, boolean thundering);

	@Unique
	private boolean lastMainConfigValue = false;

	@Unique
	private int weatherTimer = 0;

	// Runs once per world tick — keeps weather permanently in sync with the config.
	@Inject(at = {@At("HEAD")}, method = {"tick"})
	private void ewonTick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
		if (lastMainConfigValue != MainConfig.isConstantSnowfall()) {
			lastMainConfigValue = MainConfig.isConstantSnowfall();
			weatherTimer = 0;
			if (!lastMainConfigValue) {
				this.setWeatherParameters(0, 0, false, false);
			}
		}
		if (weatherTimer <= 0) {
			this.setWeatherParameters(0, 7000, MainConfig.isConstantSnowfall(), false);
			weatherTimer = 6000;
		} else {
			weatherTimer--;
		}
	}

	@Inject(at = {@At("HEAD")}, method = {"tickChunk"})
	private void ewonTickChunk(LevelChunk chunk, int randomTickSpeed, CallbackInfo info) {
		ServerLevel serverLevel = (ServerLevel)(Object)this;
		chunkSnowTick(serverLevel, chunk);
	}

	@Unique
	private static void chunkSnowTick(Level world, ChunkAccess chunk) {
		ChunkPos chunkpos = chunk.getPos();
		int i = chunkpos.getMinBlockX();
		int j = chunkpos.getMinBlockZ();
		if (world.getRandom().nextInt(100) < MainConfig.getSnowTickChance()) {
			BlockPos blockPos = findSnowTarget(world, world.getBlockRandomPos(i, 0, j, 15));
			if (blockPos == null) {
				return;
			}
			boolean configuredSnowBiome = world.getBiome(blockPos)
					.unwrapKey()
					.map(key -> MainConfig.isSnowBiome(key.location()))
					.orElse(false);
			boolean snowfallActive = MainConfig.isConstantSnowfall() || world.isRaining();
			BlockState targetState = world.getBlockState(blockPos);
			Block targetBlock = targetState.getBlock();
			if (configuredSnowBiome && snowfallActive) {
				if (targetBlock == Blocks.POWDER_SNOW) {
					if (MainConfig.isEnablePowderSnow()) {
						BlockPos p = blockPos;
						while (world.getBlockState(p).is(Blocks.POWDER_SNOW)) {
							p = p.below();
						}
						if (world.getBlockState(p).is(Blocks.SNOW_BLOCK) && world.getRandom().nextInt(100) < MainConfig.getPowderSnowChance()) {
							world.setBlockAndUpdate(p, Blocks.POWDER_SNOW.defaultBlockState());
						}
					}
					return;
				}
				if (!snowPossibleAtPosition(world, blockPos)) {
					return;
				}
				int layerDepth = Math.max(1, MainConfig.getLayerDepth());
				if (targetBlock == Blocks.SNOW) {
					int currentLayers = world.getBlockState(blockPos).getValue(SnowLayerBlock.LAYERS);
					int totalHeight = currentLayers + countSnowBlocksBelow(world, blockPos) * 8;
					if (totalHeight < layerDepth) {
						if (currentLayers < 8) {
							world.setBlockAndUpdate(blockPos, Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, currentLayers + 1));
						} else {
							if (MainConfig.isEnablePowderSnow() && world.getRandom().nextInt(100) < MainConfig.getPowderSnowChance()
								&& !world.getBlockState(blockPos.below()).is(BlockTags.LEAVES)
								&& !world.getBlockState(blockPos.below()).is(BlockTags.LOGS)) {
								world.setBlockAndUpdate(blockPos, Blocks.POWDER_SNOW.defaultBlockState());
							} else {
								world.setBlockAndUpdate(blockPos, Blocks.SNOW_BLOCK.defaultBlockState());
								world.setBlockAndUpdate(blockPos.above(), Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 1));
							}
						}
					}
				} else if (targetBlock != Blocks.SNOW_BLOCK && targetBlock != Blocks.POWDER_SNOW) {
					BlockState belowState = world.getBlockState(blockPos.below());
					if (belowState.is(Blocks.FARMLAND) && MainConfig.isBreakCropsAndFarmland()) {
						world.setBlockAndUpdate(blockPos.below(), Blocks.DIRT.defaultBlockState());
					} else if (belowState.is(Blocks.DIRT_PATH) && MainConfig.isReplacePaths()) {
						world.setBlockAndUpdate(blockPos.below(), Blocks.DIRT.defaultBlockState());
					}
					if (targetBlock instanceof TorchBlock && MainConfig.isReplaceTorches()) {
						Block.dropResources(world.getBlockState(blockPos), world, blockPos);
					}
					// Remove the upper half of tall plants before placing snow
					if (targetBlock instanceof DoublePlantBlock && MainConfig.isShouldReplaceFlowersAndGrass()) {
						DoubleBlockHalf half = targetState.getValue(DoublePlantBlock.HALF);
						if (half == DoubleBlockHalf.LOWER) {
							world.setBlockAndUpdate(blockPos.above(), Blocks.AIR.defaultBlockState());
						} else {
							world.setBlockAndUpdate(blockPos.below(), Blocks.AIR.defaultBlockState());
						}
					}
					world.setBlockAndUpdate(blockPos, Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 1));
				}
			}
		}
	}

	@Unique
	private static BlockPos findSnowTarget(Level world, BlockPos samplePos) {
		BlockPos motionBlocking = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, samplePos);
		BlockPos noLeaves = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, samplePos);

		// No tree overhead — use simple position.
		if (motionBlocking.equals(noLeaves)) {
			return motionBlocking;
		}

		// Canopy/leaf surface: use MOTION_BLOCKING which points to the air above the leaves,
		// where snow can stack normally.
		// 50% of the time use the canopy surface, 50% search below for ground/branch.
		if (world.getBlockState(noLeaves).is(BlockTags.LEAVES)) {
			if (world.getRandom().nextBoolean()) {
				return motionBlocking;
			}
		}

		// Prefer the real ground under the canopy instead of the branch itself.
		// A branch is only used as a low-probability fallback when no valid ground is found
		// within a short downward search and the branch has air above it.
		BlockPos p = noLeaves;
		BlockState prev = world.getBlockState(p.above());
		for (int depth = 0; depth < 20 && p.getY() > world.getMinBuildHeight(); depth++) {
			BlockState cur = world.getBlockState(p);

			if (cur.is(BlockTags.LOGS)) {
				if (prev.isAir() && branchFallbackChance(p)) {
					return p.above();
				}
			} else if (!cur.is(BlockTags.LEAVES) && !cur.isAir()
					&& !cur.is(Blocks.POWDER_SNOW)
					&& !cur.canBeReplaced()
					&& !cur.is(BlockTags.SMALL_FLOWERS)
					&& !cur.is(BlockTags.FLOWERS)) {
				return p.above();
			}

			prev = cur;
			p = p.below();
		}

		if (world.getBlockState(noLeaves).is(Blocks.POWDER_SNOW)) {
			return null;
		}
		return noLeaves;
	}

	@Unique
	private static boolean branchFallbackChance(BlockPos pos) {
		long hash = 0x9E3779B97F4A7C15L * pos.getX()
				+ 0xBF58476D1CE4E5B9L * pos.getY()
				+ 0x94D049BB133111EBL * pos.getZ();
		hash ^= hash >>> 32;
		hash *= 0x27D4EB2F165667C5L;
		hash ^= hash >>> 32;
		long bucket = Math.floorMod(hash, 100L);
		return bucket < 30L;
	}

	@Unique
	private static boolean isIce(Block block) {
		return block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.BLUE_ICE || block == Blocks.FROSTED_ICE;
	}

	@Unique
	private static int countSnowBlocksBelow(Level world, BlockPos pos) {
		int count = 0;
		BlockPos p = pos.below();
		while (world.getBlockState(p).is(Blocks.SNOW_BLOCK)) {
			count++;
			p = p.below();
		}
		return count;
	}

	@Unique
	private static boolean snowPossibleAtPosition(Level world, BlockPos blockPos) {
		BlockState belowState = world.getBlockState(blockPos.below());
		Block below = belowState.getBlock();
		Block target = world.getBlockState(blockPos).getBlock();
		if (isIce(below) || isIce(target)
				|| below instanceof LiquidBlock || target instanceof LiquidBlock
				|| (below instanceof DirtPathBlock && !MainConfig.isReplacePaths())
				|| below instanceof KelpBlock || below instanceof KelpPlantBlock
				|| below instanceof SeagrassBlock || below instanceof TallSeagrassBlock
				|| below instanceof StairBlock
				|| below instanceof FenceBlock || below instanceof FenceGateBlock || below instanceof WallBlock
				|| (below instanceof SlabBlock && belowState.getValue(SlabBlock.TYPE) == SlabType.BOTTOM)) {
			return false;
		}
		if (blockPos.getY() >= world.getMinBuildHeight() && blockPos.getY() < world.getMaxBuildHeight()) {
			BlockState blockstate = world.getBlockState(blockPos);
			return (blockstate.getBlock() == Blocks.SNOW) ||
					blockstate.isAir() ||
					(((blockstate.canBeReplaced() || blockstate.is(BlockTags.SMALL_FLOWERS)) && !blockstate.isAir()) && MainConfig.isShouldReplaceFlowersAndGrass()) ||
					(blockstate.getBlock() instanceof MushroomBlock && MainConfig.isReplaceSmallMushrooms()) ||
					(blockstate.is(BlockTags.FLOWERS) && MainConfig.isReplaceTallFlowers()) ||
					((blockstate.getBlock() instanceof CropBlock || blockstate.getBlock() instanceof StemBlock || blockstate.getBlock() instanceof SweetBerryBushBlock) && belowState.is(Blocks.FARMLAND) && MainConfig.isBreakCropsAndFarmland()) ||
					(blockstate.getBlock() instanceof TorchBlock && MainConfig.isReplaceTorches());
		}
		return false;
	}
}
