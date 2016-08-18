package mrriegel.chegen;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.google.common.collect.Lists;

public class Chest {

	List<Stack> items;
	List<String> biomes;
	boolean light;
	int chance, minY, maxY;
	transient String name;

	public Chest(List<Stack> items, List<String> biomes, boolean light, int chance, int minY, int maxY, String name) {
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

		/* Poti potion; */

		public Stack(String modID, String name, int minMeta, int maxMeta, int minSize, int maxSize, int chance, List<Enchantment> enchantments// ,
																																				// Poti
																																				// potion
		) {
			super();
			this.modID = modID;
			this.name = name;
			this.minMeta = minMeta;
			this.maxMeta = maxMeta;
			this.minSize = minSize;
			this.maxSize = maxSize;
			this.chance = chance;
			this.enchantments = enchantments;
			/* this.potion = potion; */
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
				for (Entry<net.minecraft.enchantment.Enchantment, Integer> e : EnchantmentHelper.getEnchantments(s).entrySet()) {
					lis.add(new Enchantment(e.getKey().getEnchantmentID(e.getKey()), e.getValue()));
				}

				return lis;
			}

			public static ItemStack enchantItemStack(Enchantment e, ItemStack s) {
				s.addEnchantment(net.minecraft.enchantment.Enchantment.getEnchantmentByID(e.id), e.strength);
				return s;
			}

		}

		// public static class Poti {
		// String modID, name;
		//
		// public Poti(String modID, String name) {
		// this.modID = modID;
		// this.name = name;
		// }
		//
		// public static Poti getPotion(ItemStack s) {
		// if(!s.hasTagCompound()||!s.getTagCompound().hasKey("Potion"))
		// return null;
		// ResourceLocation r=new ResourceLocation(PotionUtils., resourcePathIn)
		// return new Poti(modID, name)
		// if (EnchantmentHelper.getEnchantments(s).entrySet().size() == 0)
		// return null;
		// List<Enchantment> lis = Lists.newArrayList();
		// for (Entry<net.minecraft.enchantment.Enchantment, Integer> e :
		// EnchantmentHelper.getEnchantments(s).entrySet()) {
		// lis.add(new Enchantment(e.getKey().getEnchantmentID(e.getKey()),
		// e.getValue()));
		// }
		//
		// return lis;
		// }
		//
		// public static ItemStack enchantItemStack(Poti e, ItemStack s) {
		// PotionUtils.addPotionToItemStack(s,
		// PotionType.getPotionTypeForName(e.modID + ":" + e.name));
		// return s;
		// }
		//
		// }

		public static Stack getStack(ItemStack s) {
			return new Stack(s.getItem().getRegistryName().getResourceDomain(), s.getItem().getRegistryName().getResourcePath(), s.getItemDamage(), s.getItemDamage(), s.stackSize, s.stackSize, 100, Enchantment.getEnchantments(s));
		}

		public ItemStack getItemStack() {
			Random rand = new Random();
			if (rand.nextInt(100) >= chance)
				return null;
			// Item i = GameRegistry.findItem(modID, name);
			Item i = Item.REGISTRY.getObject(new ResourceLocation(modID, name));
			ItemStack res = null;
			if (i != null) {
				int size = rand.nextInt((maxSize - minSize) + 1) + minSize;
				int meta = rand.nextInt((maxMeta - minMeta) + 1) + minMeta;
				res = new ItemStack(i, size, meta);
				if (enchantments != null)
					for (Enchantment e : enchantments)
						res = Enchantment.enchantItemStack(e, res);
			}
			return res;
		}
	}

	public boolean matchBiome(World world, BlockPos pos) {
		if (biomes.contains("anywhere"))
			return true;
		if (biomes.contains(world.provider.getDimensionType().toString().toLowerCase()))
			return true;
		String currentBiom = world.getChunkFromBlockCoords(pos).getBiome(pos, world.getBiomeProvider()).getBiomeName();
		return biomes.contains(currentBiom);
	}

	public void fill(TileEntityChest tile) {
		for (Stack s : items) {
			int index = tile.getWorld().rand.nextInt(tile.getSizeInventory());
			while (tile.getStackInSlot(index) != null) {
				index = tile.getWorld().rand.nextInt(tile.getSizeInventory());
			}
			tile.setInventorySlotContents(index, s.getItemStack());
		}
	}

}
