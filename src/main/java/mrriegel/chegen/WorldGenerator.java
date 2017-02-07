package mrriegel.chegen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		for (Chest chest : ChestGenerator.instance.chests) {
			if (random.nextInt(600) >= chest.chance)
				continue;
			for (int i = 0; i < 10; ++i) {
				int j = chunkX * 16 + world.rand.nextInt(6) - world.rand.nextInt(6);
				int k = chunkZ * 16 + world.rand.nextInt(6) - world.rand.nextInt(6);
				BlockPos blockpos = new BlockPos(j, chest.minY, k);
				if (!chest.matchBiome(world, blockpos))
					continue;
				int max = Math.min(chest.maxY, world.getActualHeight());
				while (true) {
					if (blockpos.getY() < max && world.getBlockState(blockpos.down()).getMaterial().blocksMovement() && world.isAirBlock(blockpos))
						break;
					blockpos = blockpos.up();
					if (blockpos.getY() > max)
						break;
				}
				if (blockpos.getY() > max)
					continue;
				boolean foo = true;
				for (int ii = 0; ii < 3; ii++)
					if (!world.isAirBlock(blockpos.up(ii + 1)))
						foo = false;
				if (foo && generate(world, world.rand, blockpos, chest)) {
					break;
				}
			}
		}
	}

	private boolean generate(World world, Random rand, BlockPos position, Chest chest) {
		BlockPos blockpos = new BlockPos(position);
		world.setBlockState(blockpos, Blocks.CHEST.getStateFromMeta(rand.nextInt(6)), 2);
		if (ConfigHandler.debugOutput)
			ChestGenerator.logger.info("Chest " + chest.name + " at " + blockpos);
		TileEntity tileentity = world.getTileEntity(blockpos);

		if (tileentity instanceof TileEntityChest) {
			chest.fill((TileEntityChest) tileentity);
		}

		BlockPos blockpos1 = blockpos.east();
		BlockPos blockpos2 = blockpos.west();
		BlockPos blockpos3 = blockpos.north();
		BlockPos blockpos4 = blockpos.south();
		if (chest.light) {
			if (world.isAirBlock(blockpos2) && world.getBlockState(blockpos2.down()).getMaterial().blocksMovement()) {
				world.setBlockState(blockpos2, Blocks.TORCH.getDefaultState(), 2);
			}
			if (world.isAirBlock(blockpos1) && world.getBlockState(blockpos1.down()).getMaterial().blocksMovement()) {
				world.setBlockState(blockpos1, Blocks.TORCH.getDefaultState(), 2);
			}
			if (world.isAirBlock(blockpos3) && world.getBlockState(blockpos3.down()).getMaterial().blocksMovement()) {
				world.setBlockState(blockpos3, Blocks.TORCH.getDefaultState(), 2);
			}
			if (world.isAirBlock(blockpos4) && world.getBlockState(blockpos4.down()).getMaterial().blocksMovement()) {
				world.setBlockState(blockpos4, Blocks.TORCH.getDefaultState(), 2);
			}
		}
		return true;

	}

}
