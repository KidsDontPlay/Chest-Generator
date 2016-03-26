package mrriegel.chegen;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Chest {

	List<Stack> items;
	List<String> biomes;
	boolean light;
	int chance;

	public Chest(List<Stack> items, List<String> biomes, boolean light,
			int chance) {
		super();
		this.items = items;
		this.biomes = biomes;
		this.light = light;
		this.chance = chance;
	}

	public static class Stack {
		String modID, name;
		int meta, minSize, maxSize, chance;
		List<Enchantment> enchantments;

		public Stack(String modID, String name, int meta, int minSize,
				int maxSize, int chance, List<Enchantment> enchantments) {
			super();
			this.modID = modID;
			this.name = name;
			this.meta = meta;
			this.minSize = minSize;
			this.maxSize = maxSize;
			this.chance = chance;
			this.enchantments = enchantments;
		}

		public static class Enchantment {
			int id;
			int strength;

			public Enchantment(int id, int strength) {
				super();
				this.id = id;
				this.strength = strength;
			}

			public static List<Enchantment> getEnchantments(ItemStack s) {
				List<Enchantment> lis = Lists.newArrayList();
				for (Entry<Integer, Integer> e : EnchantmentHelper
						.getEnchantments(s).entrySet()) {
					lis.add(new Enchantment(e.getKey(), e.getValue()));
				}

				return lis;
			}

			public static ItemStack getItemStack(Enchantment s) {
				
				return net.minecraft.enchantment.Enchantment
						.getEnchantmentById(s.id);
			}

		}

		public static Stack getStack(ItemStack s) {
			String[] ar = s.getItem().getRegistryName().split(":");
			return new Stack(ar[0], ar[1], s.getItemDamage(), s.stackSize,
					s.stackSize, 100, null);
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
				res = new ItemStack(i, size, s.meta);
			}
			return res;
		}
	}

}
