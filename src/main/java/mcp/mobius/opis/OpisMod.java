package mcp.mobius.opis;

import java.util.UUID;
import mcp.mobius.opis.data.profilers.ProfilerSection;
import mcp.mobius.opis.commands.client.CommandOpis;
import mcp.mobius.opis.commands.server.*;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.profilers.*;
import mcp.mobius.opis.events.*;
import mcp.mobius.opis.helpers.ModIdentification;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.*;
import mcp.mobius.opis.proxy.CommonProxy;
import mcp.mobius.opis.tools.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.*;

@Mod(name = OpisMod.NAME, modid = OpisMod.MODID, version = OpisMod.VERSION, dependencies = "", acceptableRemoteVersions = "*")

public class OpisMod {

    public static final String NAME = "OpisReloaded";
    public static final String MODID = "opisreloaded";
    public static final String VERSION = "1.1.3";

    @Mod.Instance(OpisMod.MODID)
    public static OpisMod instance;

    public static final Logger LOGGER = LogManager.getLogger("Opis");

    @SidedProxy(clientSide = "mcp.mobius.opis.proxy.ProxyClient", serverSide = "mcp.mobius.opis.proxy.ProxyServer")
    public static CommonProxy proxy;

    public static int profilerDelay = 1;
    public static boolean profilerRun = false;
    public static boolean profilerRunClient = false;
    public static int profilerMaxTicks = 250;
    public static boolean microseconds = true;
    private static int lagGenID = -1;
    public static CoordinatesBlock selectedBlock = null;

    public static boolean mappingEnabled = false;

    public Configuration config = null;

    public static String commentTables = "Minimum access level to be able to view tables in /opis command. Valid values : NONE, PRIVILEGED, ADMIN";
    public static String commentOpis = "Minimum access level to be open Opis interface. Valid values : NONE, PRIVILEGED, ADMIN";
    public static String commentPrivileged = "List of players with PRIVILEGED access level.";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());

        profilerDelay = config.get(Configuration.CATEGORY_GENERAL, "profiler.delay", 1).getInt();
        lagGenID = config.get(Configuration.CATEGORY_GENERAL, "laggenerator_id", -1).getInt();
        profilerMaxTicks = config.get(Configuration.CATEGORY_GENERAL, "profiler.maxpts", 250).getInt();
        microseconds = config.get(Configuration.CATEGORY_GENERAL, "display.microseconds", true).getBoolean(true);

        String[] users = config.get("ACCESS_RIGHTS", "privileged", new String[]{}, commentPrivileged).getStringList();
        AccessLevel minTables = AccessLevel.PRIVILEGED;
        AccessLevel openOpis = AccessLevel.PRIVILEGED;
        try {
            openOpis = AccessLevel.valueOf(config.get("ACCESS_RIGHTS", "opis", "NONE", commentTables).getString());
        } catch (IllegalArgumentException e) {
        }
        try {
            minTables = AccessLevel.valueOf(config.get("ACCESS_RIGHTS", "tables", "NONE", commentTables).getString());
        } catch (IllegalArgumentException e) {
        }

        Message.setTablesMinimumLevel(minTables);
        Message.setOpisMinimumLevel(openOpis);

        for (String user : users) {
            PlayerTracker.INSTANCE.addPrivilegedPlayer(UUID.fromString(user), false);
        }

        config.save();

        MinecraftForge.EVENT_BUS.register(new OpisClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new OpisServerEventHandler());
        FMLCommonHandler.instance().bus().register(OpisClientTickHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(OpisServerTickHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(PlayerTracker.INSTANCE);

        PacketManager.init();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (lagGenID != -1) {
            Block blockDemo = new BlockLag(Material.WOOD);
            GameRegistry.register(blockDemo);
            ItemBlock itemDemo = new ItemBlock(blockDemo);
            itemDemo.setRegistryName("opis.laggen");
            GameRegistry.register(itemDemo);
            GameRegistry.registerTileEntity(TileLag.class, "opis.laggen");

            Block blockDebug = new BlockDebug(Material.WOOD);
            GameRegistry.register(blockDebug);
            ItemBlock itemDebug = new ItemBlock(blockDebug);
            itemDebug.setRegistryName("opis.debug");
            GameRegistry.register(itemDebug);
            GameRegistry.registerTileEntity(TileDebug.class, "opis.debug");
        }

        ProfilerSection.RENDER_TILEENTITY.setProfiler(new ProfilerRenderTileEntity());
        ProfilerSection.RENDER_ENTITY.setProfiler(new ProfilerRenderEntity());
        ProfilerSection.RENDER_BLOCK.setProfiler(new ProfilerRenderBlock());
        ProfilerSection.EVENT_INVOKE.setProfiler(new ProfilerEvent());
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        ProfilerSection.DIMENSION_TICK.setProfiler(new ProfilerDimTick());
        ProfilerSection.DIMENSION_BLOCKTICK.setProfiler(new ProfilerDimBlockTick());
        ProfilerSection.ENTITY_UPDATETIME.setProfiler(new ProfilerEntityUpdate());
        ProfilerSection.TICK.setProfiler(new ProfilerTick());
        ProfilerSection.TILEENT_UPDATETIME.setProfiler(new ProfilerTileEntityUpdate());
        ProfilerSection.PACKET_INBOUND.setProfiler(new ProfilerPacket());
        ProfilerSection.PACKET_OUTBOUND.setProfiler(new ProfilerPacket());
        ProfilerSection.NETWORK_TICK.setProfiler(new ProfilerNetworkTick());

        event.registerServerCommand(new CommandFrequency());
        event.registerServerCommand(new CommandStop());
        event.registerServerCommand(new CommandTicks());
        event.registerServerCommand(new CommandKill());
        event.registerServerCommand(new CommandReset());
        event.registerServerCommand(new CommandEntityCreate());

        event.registerServerCommand(new CommandOpis());
        event.registerServerCommand(new CommandAddPrivileged());
        event.registerServerCommand(new CommandRmPrivileged());
    }
}
