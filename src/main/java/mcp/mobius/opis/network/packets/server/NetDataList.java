package mcp.mobius.opis.network.packets.server;

import com.google.common.io.*;
import mcp.mobius.opis.api.MessageHandlerRegistrar;
import mcp.mobius.opis.data.holders.*;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.*;

public class NetDataList extends PacketBase {

    public NetDataList() {
    }

    public NetDataList(Message msg, List<? extends ISerializable> data) {
        this.msg = msg;
        this.array = new ArrayList<>(data);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeInt(this.msg.ordinal());
        output.writeInt(this.array.size());

        if (this.array.size() > 0) {
            output.writeInt(DataType.getForClass(this.array.get(0).getClass()).ordinal());
        }

        this.array.forEach((odata) -> {
            odata.writeToStream(output);
        });
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        this.msg = Message.values()[input.readInt()];
        int ndata = input.readInt();
        this.clazzStr = "";
        if (ndata > 0) {
            this.clazz = DataType.getForOrdinal(input.readInt());
        }

        this.array = new ArrayList<>();

        for (int i = 0; i < ndata; i++) {
            this.array.add(dataRead(this.clazz, input));
        }
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        MessageHandlerRegistrar.INSTANCE.routeMessage(this.msg, this);
    }

}
