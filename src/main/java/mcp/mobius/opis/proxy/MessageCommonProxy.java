package mcp.mobius.opis.proxy;

import mcp.mobius.opis.api.IMessageHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod.EventBusSubscriber
public abstract class MessageCommonProxy implements IMessageHandler {
    public void init(FMLInitializationEvent event){
    }
}