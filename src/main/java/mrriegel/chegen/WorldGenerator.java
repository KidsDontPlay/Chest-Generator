package mrriegel.chegen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenerator implements IWorldGenerator {
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		for (Chest chest : ChestGenerator.instance.chests) {
			if (random.nextInt(600) >= chest.chance)
				return;
			for (int i = 0; i < 10; ++i) {
				int j = chunkX * 16 + world.rand.nextInt(6)
						- world.rand.nextInt(6);
				int k = chunkZ * 16 + world.rand.nextInt(6)
						- world.rand.nextInt(6);
				BlockPos blockpos = new BlockPos(j, chest.minY, k);
				int max = Math.min(chest.maxY, 256);
				while (true) {
					if (blockpos.getY() < max
							&& World.doesBlockHaveSolidTopSurface(world,
									blockpos.down())
							&& world.isAirBlock(blockpos))
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
				if (foo && chest.matchBiome(world, blockpos)
						&& generate(world, world.rand, blockpos, chest)) {
					break;
				}
			}
		}
	}

	private boolean generate(World world, Random rand, BlockPos position,
			Chest chest) {

		BlockPos blockpos = new BlockPos(position);
		world.setBlockState(blockpos, Blocks.chest.getDefaultState(), 2);
		System.out.println("set: " + position);
		TileEntity tileentity = world.getTileEntity(blockpos);

		if (tileentity instanceof TileEntityChest) {
			chest.fill((TileEntityChest) tileentity);
		}

		BlockPos blockpos1 = blockpos.east();
		BlockPos blockpos2 = blockpos.west();
		BlockPos blockpos3 = blockpos.north();
		BlockPos blockpos4 = blockpos.south();
		if (chest.light) {
			if (world.isAirBlock(blockpos2)
					&& World.doesBlockHaveSolidTopSurface(world,
							blockpos2.down())) {
				world.setBlockState(blockpos2, Blocks.torch.getDefaultState(),
						2);
			}

			if (world.isAirBlock(blockpos1)
					&& World.doesBlockHaveSolidTopSurface(world,
							blockpos1.down())) {
				world.setBlockState(blockpos1, Blocks.torch.getDefaultState(),
						2);
			}

			if (world.isAirBlock(blockpos3)
					&& World.doesBlockHaveSolidTopSurface(world,
							blockpos3.down())) {
				world.setBlockState(blockpos3, Blocks.torch.getDefaultState(),
						2);
			}

			if (world.isAirBlock(blockpos4)
					&& World.doesBlockHaveSolidTopSurface(world,
							blockpos4.down())) {
				world.setBlockState(blockpos4, Blocks.torch.getDefaultState(),
						2);
			}
		}
		return true;

	}
}
