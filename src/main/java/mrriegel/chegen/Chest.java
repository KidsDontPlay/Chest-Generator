package mrriegel.chegen;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.google.common.collect.Lists;

public class Chest {

	List<Stack> items;
	List<String> biomes;
	boolean light;
	int chance, minY, maxY;
	transient String name;

	public Chest(List<Stack> items, List<String> biomes, boolean light,
			int chance, int minY, int maxY, String name) {
		this.items = items;
		this.biomes = biomes;
		this.light = light;
		this.chance = chance;
		this.minY = minY;
		this.maxY = maxY;
		this.name = name;
	}

	public static class Stack {
		String modID, name;
		int minMeta, maxMeta, minSize, maxSize, chance;
		List<Enchantment> enchantments;

		public Stack(String modID, String name, int minMeta, int maxMeta,
				int minSize, int maxSize, int chance,
				List<Enchantment> enchantments) {
			super();
			this.modID = modID;
			this.name = name;
			this.minMeta = minMeta;
			this.maxMeta = maxMeta;
			this.minSize = minSize;
			this.maxSize = maxSize;
			this.chance = chance;
			this.enchantments = enchantments;
		}

		public static class Enchantment {
			int id;
			int strength;

			public Enchantment(int id, int strength) {
				this.id = id;
				this.strength = strength;
			}

			public static List<Enchantment> getEnchantments(ItemStack s) {
				if (EnchantmentHelper.getEnchantments(s).entrySet().size() == 0)
					return null;
				List<Enchantment> lis = Lists.newArrayList();
				for (Entry<Integer, Integer> e : EnchantmentHelper
						.getEnchantments(s).entrySet()) {
					lis.add(new Enchantment(e.getKey(), e.getValue()));
				}

				return lis;
			}

			public static ItemStack enchantItemStack(Enchantment e, ItemStack s) {
				s.addEnchantment(net.minecraft.enchantment.Enchantment
						.getEnchantmentById(e.id), e.strength);
				return s;
			}

		}

		public static Stack getStack(ItemStack s) {
			String[] ar = s.getItem().getRegistryName().split(":");
			Stack stack = new Stack(ar[0], ar[1], s.getItemDamage(),
					s.getItemDamage(), s.stackSize, s.stackSize, 100, null);
			stack.enchantments = Enchantment.getEnchantments(s);
			return stack;
		}

		public static ItemStack getItemStack(Stack s) {
			Random rand = new Random();
			if (rand.nextInt(100) >= s.chance)
				return null;
			Item i = GameRegistry.findItem(s.modID, s.name);
			ItemStack res = null;
			if (i != null) {
				int size = rand.nextInt((s.maxSize - s.minSize) + 1)
						+ s.minSize;
				int meta = rand.nextInt((s.maxMeta - s.minMeta) + 1)
						+ s.minMeta;
				res = new ItemStack(i, size, meta);
				if (s.enchantments != null)
					for (Enchantment e : s.enchantments)
						res = Enchantment.enchantItemStack(e, res);
			}
			return res;
		}
	}

	public boolean matchBiome(World world, BlockPos pos) {
		if (biomes.contains("anywhere"))
			return true;
		if (biomes.contains(world.provider.getDimensionName().toLowerCase()))
			return true;
		String currentBiom = world.getWorldChunkManager().getBiomeGenerator(
				new BlockPos(pos.getX(), 0, pos.getZ())).biomeName
				.toLowerCase();
		return biomes.contains(currentBiom);
	}

	public void fill(TileEntityChest tile) {
		for (Stack s : items) {
			int index = tile.getWorld().rand.nextInt(tile.getSizeInventory());
			while (tile.getStackInSlot(index) != null) {
				index = tile.getWorld().rand.nextInt(tile.getSizeInventory());
			}
			tile.setInventorySlotContents(index, Stack.getItemStack(s));
		}
	}

}
