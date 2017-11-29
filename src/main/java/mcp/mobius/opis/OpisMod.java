package mcp.mobius.opis;

import mcp.mobius.opis.commands.client.CommandOpis;
import mcp.mobius.opis.commands.server.*;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.profilers.*;
import mcp.mobius.opis.events.*;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.proxy.MessageCommonProxy;
import mcp.mobius.opis.tools.BlockDebug;
import mcp.mobius.opis.tools.BlockLag;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(name = OpisMod.NAME, modid = OpisMod.MODID, version = OpisMod.VERSION, acceptableRemoteVersions = "*")

public class OpisMod {

    public static final String NAME = "OpisReloaded";
    public static final String MODID = "opisreloaded";
    public static final String VERSION = "1.1.5";

    @Mod.Instance(OpisMod.MODID)
    public static OpisMod instance;

    public static final Logger LOGGER = LogManager.getLogger("Opis");

    public static Configuration config;

    @SidedProxy(clientSide = "mcp.mobius.opis.proxy.MessageClientProxy", serverSide = "mcp.mobius.opis.proxy.MessageServerProxy")
    public static MessageCommonProxy messageProxy;

    public static boolean profilerRun = false;
    public static boolean profilerRunClient = false;

    public static CoordinatesBlock selectedBlock = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File directory = event.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "opisreloaded.cfg"));
        OpisConfig.readConfig();

        MinecraftForge.EVENT_BUS.register(new OpisClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new OpisServerEventHandler());
        MinecraftForge.EVENT_BUS.register(OpisClientTickHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(OpisServerTickHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(PlayerTracker.INSTANCE);

        PacketManager.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ProfilerSection.RENDER_TILEENTITY.setProfiler(new ProfilerRenderTileEntity());
        ProfilerSection.RENDER_ENTITY.setProfiler(new ProfilerRenderEntity());
        ProfilerSection.RENDER_BLOCK.setProfiler(new ProfilerRenderBlock());
        ProfilerSection.EVENT_INVOKE.setProfiler(new ProfilerEvent());
        messageProxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (config.hasChanged()) {
            config.save();
        }
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

        event.registerServerCommand(new CommandStop());
        event.registerServerCommand(new CommandTicks());
        event.registerServerCommand(new CommandKill());
        event.registerServerCommand(new CommandReset());
        event.registerServerCommand(new CommandEntityCreate());

        event.registerServerCommand(new CommandOpis());
        event.registerServerCommand(new CommandAddPrivileged());
        event.registerServerCommand(new CommandRmPrivileged());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        if (OpisConfig.enableDebugBlocks) {
            event.getRegistry().register(new BlockLag());
            event.getRegistry().register(new BlockDebug());
        }
    }
}
