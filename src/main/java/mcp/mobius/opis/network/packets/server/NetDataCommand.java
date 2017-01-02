package mcp.mobius.opis.network.packets.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import mcp.mobius.opis.api.MessageHandlerRegistrar;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;

public class NetDataCommand extends PacketBase {

    public NetDataCommand(Message msg) {
        this.msg = msg;
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeInt(this.msg.ordinal());
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        this.msg = Message.values()[input.readInt()];
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        MessageHandlerRegistrar.INSTANCE.routeMessage(this.msg, this);
    }

}
