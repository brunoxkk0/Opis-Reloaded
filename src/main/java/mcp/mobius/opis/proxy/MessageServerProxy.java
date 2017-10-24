package mcp.mobius.opis.proxy;

import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class MessageServerProxy extends MessageCommonProxy {
    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        return false;
    }
}
