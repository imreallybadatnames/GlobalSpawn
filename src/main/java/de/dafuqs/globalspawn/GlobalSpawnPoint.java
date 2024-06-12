package de.dafuqs.globalspawn;

import net.minecraft.block.*;
import net.minecraft.nbt.*;
import net.minecraft.registry.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class GlobalSpawnPoint {

	private final RegistryKey<World> dimension;
	private final BlockPos position;
	private final int horizontalSpread;
	private final SpawnCriterion criterion;
	private final float angle;

	public enum SpawnCriterion {
		SAFE_WITH_SKY_ACCESS,
		UNOBSTRUCTED_Y,
		EXACT
	}

	public GlobalSpawnPoint(RegistryKey<World> spawnPointDimension, BlockPos position, int spread, SpawnCriterion criterion, float angle) {
		this.dimension = spawnPointDimension;
		this.position = position;
		this.criterion = criterion;
		this.angle = angle;
		this.horizontalSpread = spread;
	}

	public NbtCompound getSpawnNbtCompound(MinecraftServer server, @Nullable NbtCompound nbtCompound) {
		if (nbtCompound == null) {
			nbtCompound = new NbtCompound();
		}

		@Nullable BlockPos spawnPos = getFinalSpawnPos(server);
		if (spawnPos == null) {
			return nbtCompound;
		}

		nbtCompound.putString("Dimension", dimension.getValue().toString());
		nbtCompound.putFloat("SpawnAngle", angle);

		NbtList listTag = new NbtList();
		listTag.addElement(0, NbtDouble.of(spawnPos.getX() + 0.5));
		listTag.addElement(1, NbtDouble.of(spawnPos.getY()));
		listTag.addElement(2, NbtDouble.of(spawnPos.getZ() + 0.5));
		nbtCompound.put("Pos", listTag);

		return nbtCompound;
	}

	public RegistryKey<World> getDimension() {
		return dimension;
	}

	public BlockPos getPos() {
		return this.position;
	}

	public int getHorizontalSpread() {
		return horizontalSpread;
	}

	public SpawnCriterion getCriterion() {
		return this.criterion;
	}

	public float getAngle() {
		return angle;
	}

	public @Nullable BlockPos getFinalSpawnPos(MinecraftServer server) {
		ServerWorld world = server.getWorld(dimension);
		if (world == null) {
			return null;
		}

		Random random = world.getRandom();

		BlockPos pos = new BlockPos(
				MathHelper.nextBetween(random, this.position.getX() - this.horizontalSpread, this.position.getX() + this.horizontalSpread),
				this.position.getY(),
				MathHelper.nextBetween(random, this.position.getZ() - this.horizontalSpread, this.position.getZ() + this.horizontalSpread)
		);

		switch (this.criterion) {
			case SAFE_WITH_SKY_ACCESS -> {
				return SpawnLocating.findServerSpawnPoint(world, new ChunkPos(pos));
			}
			case UNOBSTRUCTED_Y -> {
				return findSpawn(world, pos);
			}
			case EXACT -> {
				return pos;
			}
		}

		return null;
	}

	// See SpawnLocating.findOverworldSpawn for reference
	@Nullable
	protected BlockPos findSpawn(ServerWorld world, BlockPos pos) {
		int y = this.position.getY();
		if (y < world.getBottomY()) {
			return null;
		}

		BlockPos.Mutable mutablePos = new BlockPos.Mutable();
		for (int currY = y + 1; currY >= world.getBottomY(); --currY) {
			mutablePos.set(pos.getX(), currY, pos.getZ());
			BlockState blockState = world.getBlockState(mutablePos);
			if (!blockState.getFluidState().isEmpty()) {
				return null;
			}

			if (Block.isFaceFullSquare(blockState.getCollisionShape(world, mutablePos), Direction.UP)) {
				return mutablePos.up().toImmutable();
			}
		}

		return null;
	}

}