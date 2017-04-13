package mcp.mobius.opis.data.profilers;

import java.util.HashMap;
import mcp.mobius.opis.OpisMod;

import net.minecraft.network.Packet;
import mcp.mobius.opis.data.holders.newtypes.DataPacket;
import mcp.mobius.opis.data.holders.newtypes.DataPacket250;
import mcp.mobius.opis.helpers.Helpers;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

public class ProfilerPacket extends ProfilerAbstract {

    public long dataAmount = 0;
    public HashMap<String, DataPacket> data = new HashMap<>();
    public HashMap<String, DataPacket250> data250 = new HashMap<>();

    public void startInterval() {
        data.values().forEach((packet) -> {
            packet.startInterval();
        });

        data250.values().forEach((packet) -> {
            packet.startInterval();
        });
    }

    @Override
    public void reset() {
        dataAmount = 0;
    }

    @Override
    public void start(Object msg, Object size) {
        if ((msg != null) && (Helpers.getEffectiveSide() == Side.SERVER)) {
            if (!(msg instanceof FMLProxyPacket)) {
                Packet pkt = (Packet) msg;

                dataAmount += (Integer) size;

                try {
                    data.get(msg.getClass().getSimpleName()).fill(pkt, (Integer) size);
                } catch (Exception e) {
                    data.put(msg.getClass().getSimpleName(), new DataPacket((Packet) msg));
                    data.get(msg.getClass().getSimpleName()).fill(pkt, (Integer) size);
                }
            }
        }
    }

    @Override
    public void stop(Object msg) {
        try {
            if ((msg != null) && (Helpers.getEffectiveSide() == Side.SERVER)) {
                if (msg instanceof FMLProxyPacket) {
                    FMLProxyPacket pkt = (FMLProxyPacket) msg;
                    String channel = pkt.channel();
                    int pktsize = pkt.payload().capacity();
                    dataAmount += pktsize;

                    try {
                        data250.get(channel).fill(pkt, pktsize);
                    } catch (Exception e) {
                        data250.put(channel, new DataPacket250(channel));
                        data250.get(channel).fill(pkt, pktsize);
                    }
                }
            }
        } catch (Exception e) {
            OpisMod.LOGGER.warn(String.format("Error while profiling packet %s\n", msg));
        }
    }
}
