package mrriegel.chegen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import mrriegel.chegen.Chest.Stack;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.commons.lang3.text.WordUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

@Mod(modid = ChestGenerator.MODID, name = ChestGenerator.MODNAME, version = ChestGenerator.VERSION)
public class ChestGenerator {
	public static final String MODID = "chegen";
	public static final String VERSION = "1.0.0";
	public static final String MODNAME = "Chest Generator";

	@Instance(ChestGenerator.MODID)
	public static ChestGenerator instance;

	public List<Chest> chests = Lists.newArrayList();
	public File configDir;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException {
		configDir = new File(event.getModConfigurationDirectory(),
				ChestGenerator.MODNAME);
		ConfigHandler.refreshConfig(new File(configDir, "config.cfg"));
		initFiles();
	}

	private void initFiles() throws JsonIOException, JsonSyntaxException,
			FileNotFoundException {
		List<File> files = new ArrayList<File>();
		for (final File fileEntry : configDir.listFiles()) {
			if (fileEntry.getName().endsWith(".json"))
				files.add(fileEntry);
		}

		for (File f : files) {
			Chest chest = new Gson().fromJson(new BufferedReader(
					new FileReader(f)), new TypeToken<Chest>() {
			}.getType());
			chests.add(chest);
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new WorldGenerator(), 1);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		System.out.println("1: "
				+ GameRegistry.findItem("ffs", "ffs:blockEnergyValve"));
		System.out.println("2: "
				+ GameRegistry.findItem("ffs", "blockEnergyValve"));
	}

	@SubscribeEvent
	public void create(PlayerInteractEvent e) throws IOException {
		if (!e.world.isRemote && e.action == Action.RIGHT_CLICK_BLOCK
				&& e.entityPlayer.isSneaking()
				&& e.entityPlayer.capabilities.isCreativeMode
				&& e.world.getTileEntity(e.pos) instanceof TileEntityChest) {
			System.out.println(chests);
			File f = new File(configDir, (new SimpleDateFormat(
					"yyyy.MM.dd'_'HH:mm:ss")).format(new Date()) + ".json");
			f.createNewFile();
			List<Stack> stacks = Lists.newArrayList();
			TileEntityChest tile = (TileEntityChest) e.world
					.getTileEntity(e.pos);
			for (int i = 0; i < tile.getSizeInventory(); i++)
				if (tile.getStackInSlot(i) != null)
					stacks.add(Stack.getStack(tile.getStackInSlot(i)));
			String biom = e.world.provider.getDimensionName().toLowerCase();
			Chest c = new Chest(stacks, Arrays.asList(biom), true, 100);
			FileWriter fw = new FileWriter(f);
			fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(c));
			fw.close();
			initFiles();
			e.setCanceled(true);
		}
	}
}