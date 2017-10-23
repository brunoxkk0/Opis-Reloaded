package mcp.mobius.opis.network.packets.client;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.network.PacketBase;

public class PacketReqChunks extends PacketBase {

    public int dim;
    public ArrayList<CoordinatesChunk> chunks = new ArrayList<>();

    public PacketReqChunks() {
    }

    public PacketReqChunks(int dim, ArrayList<CoordinatesChunk> chunks) {
        this.dim = dim;
        this.chunks = chunks;
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeInt(dim);
        output.writeInt(chunks.size());
        chunks.forEach((coord) -> {
            coord.writeToStream(output);
        });
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        dim = input.readInt();
        int nchunks = input.readInt();
        for (int i = 0; i < nchunks; i++) {
            chunks.add(CoordinatesChunk.readFromStream(input));
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }

}
