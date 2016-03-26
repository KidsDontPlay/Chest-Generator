package mrriegel.chegen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenerator implements IWorldGenerator {
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
//		if (random.nextInt(10) == 0) {
//			BlockPos pos = new BlockPos(chunkX * 16 + random.nextInt(16),
//					random.nextInt(64) + 32, chunkZ * 16 + random.nextInt(16));
//			System.out.println("Generated lake " + pos.toString());
//			// new WorldGenLiquids(p_i45465_1_)
//			new WorldGenLakes(Blocks.stained_hardened_clay).generate(world,
//					random, pos);
//		}
	}
}
