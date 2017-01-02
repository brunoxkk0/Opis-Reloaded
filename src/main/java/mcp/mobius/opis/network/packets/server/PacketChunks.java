package mcp.mobius.opis.network.packets.server;

import com.google.common.io.*;
import mcp.mobius.opis.network.PacketBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PacketChunks extends PacketBase {

    public int dim;

    @Override
    public void encode(ByteArrayDataOutput output) {
        // TODO Auto-generated method stub

    }

    @Override
    public void decode(ByteArrayDataInput input) {
        // TODO Auto-generated method stub

    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
    }

}
