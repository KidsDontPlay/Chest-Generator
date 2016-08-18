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
import java.util.List;

import mrriegel.chegen.Chest.Stack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

@Mod(modid = ChestGenerator.MODID, name = ChestGenerator.MODNAME, version = ChestGenerator.VERSION)
public class ChestGenerator {
	public static final String MODID = "chegen";
	public static final String VERSION = "1.0.1";
	public static final String MODNAME = "Chest Generator";

	@Instance(ChestGenerator.MODID)
	public static ChestGenerator instance;

	public static Logger logger;

	public List<Chest> chests = Lists.newArrayList();
	public File configDir;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException {
		configDir = new File(event.getModConfigurationDirectory(), ChestGenerator.MODNAME);
		logger = event.getModLog();
		configDir.mkdir();
		ConfigHandler.refreshConfig(new File(configDir, "config.cfg"));
		initFiles();
	}

	private void initFiles() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		List<File> files = new ArrayList<File>();
		for (final File fileEntry : configDir.listFiles()) {
			if (fileEntry.getName().endsWith(".json"))
				files.add(fileEntry);
		}
		for (File f : files) {
			Chest chest = new Gson().fromJson(new BufferedReader(new FileReader(f)), new TypeToken<Chest>() {
			}.getType());
			if (chest.items.size() > new TileEntityChest().getSizeInventory())
				throw new IllegalArgumentException("too many items");
			chest.name = f.getName().replaceAll(".json", "");
			chests.add(chest);
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new WorldGenerator(), 30);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void create(RightClickBlock e) throws IOException {
		if (!e.getWorld().isRemote && e.getEntityPlayer().isSneaking() && e.getEntityPlayer().capabilities.isCreativeMode && e.getWorld().getTileEntity(e.getPos()) instanceof TileEntityChest) {
			File f = new File(configDir, (new SimpleDateFormat("yyyy.MM.dd'_'HH:mm:ss")).format(new Date()) + ".json");
			f.createNewFile();
			List<Stack> stacks = Lists.newArrayList();
			TileEntityChest tile = (TileEntityChest) e.getWorld().getTileEntity(e.getPos());
			for (int i = 0; i < tile.getSizeInventory(); i++)
				if (tile.getStackInSlot(i) != null)
					stacks.add(Stack.getStack(tile.getStackInSlot(i)));
			String biom = e.getWorld().provider.getDimensionType().toString().toLowerCase();
			Chest c = new Chest(stacks, Arrays.asList(biom), true, 100, 1, 256, f.getName().replaceAll(".json", ""));
			FileWriter fw = new FileWriter(f);
			fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(c));
			fw.close();
			e.getEntityPlayer().addChatComponentMessage(new TextComponentString(f.getName() + " created."));
			initFiles();
			e.setCanceled(true);
		}
	}
}