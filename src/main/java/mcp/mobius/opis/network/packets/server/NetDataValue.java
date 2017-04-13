package mcp.mobius.opis.network.packets.server;

import com.google.common.io.*;
import mcp.mobius.opis.api.MessageHandlerRegistrar;
import mcp.mobius.opis.data.holders.*;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class NetDataValue extends PacketBase {

    public NetDataValue() {
    }

    public NetDataValue(Message msg, ISerializable data) {
        this.msg = msg;
        this.value = data;
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeInt(this.msg.ordinal());
        output.writeInt(DataType.getForClass(this.value.getClass()).ordinal());
        this.value.writeToStream(output);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        this.msg = Message.values()[input.readInt()];
        this.clazz = DataType.getForOrdinal(input.readInt());
        this.value = dataRead(this.clazz, input);

    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        MessageHandlerRegistrar.INSTANCE.routeMessage(this.msg, this);
    }

}
