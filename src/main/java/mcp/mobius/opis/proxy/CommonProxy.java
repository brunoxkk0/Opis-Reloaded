package mcp.mobius.opis.proxy;

import mcp.mobius.opis.OpisMod;
import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.tools.BlockDebug;
import mcp.mobius.opis.tools.BlockLag;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public abstract class CommonProxy implements IMessageHandler {

    public abstract void preInit(FMLPreInitializationEvent event);

    public abstract void init(FMLInitializationEvent event);

    public abstract void postInit(FMLPostInitializationEvent event);

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        if (OpisMod.debugBlocks) {
            event.getRegistry().register(new BlockLag());
            event.getRegistry().register(new BlockDebug());
        }
    }
}
