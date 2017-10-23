package mcp.mobius.opis.data.profilers;

import java.util.HashMap;
import mcp.mobius.opis.OpisMod;

import mcp.mobius.opis.data.holders.newtypes.DataPacket;
import mcp.mobius.opis.helpers.Helpers;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

public class ProfilerPacket extends ProfilerAbstract {

    public long dataAmount = 0;
    public HashMap<String, DataPacket> data = new HashMap<>();

    public void startInterval() {
        data.values().forEach((packet) -> {
            packet.startInterval();
        });
    }

    @Override
    public void reset() {
        dataAmount = 0;
    }

    @Override
    public void start(Object msg) {
        try {
            if ((msg != null) && (Helpers.getEffectiveSide() == Side.SERVER)) {
                System.out.println("PKTCLASS: " + msg.getClass().toString());
                if (msg instanceof FMLProxyPacket) {
                    FMLProxyPacket pkt = (FMLProxyPacket) msg;
                    String channel = pkt.channel();
                    int pktsize = pkt.payload().capacity();
                    dataAmount += pktsize;

                    try {
                        data.get(channel).fill(pkt, pktsize);
                    } catch (Exception e) {
                        data.put(channel, new DataPacket(pkt));
                        data.get(channel).fill(pkt, pktsize);
                    }
                }
            }
        } catch (Exception e) {
            OpisMod.LOGGER.warn(String.format("Error while profiling packet %s\n", msg));
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
                        data.get(channel).fill(pkt, pktsize);
                    } catch (Exception e) {
                        data.put(channel, new DataPacket(pkt));
                        data.get(channel).fill(pkt, pktsize);
                    }
                }
            }
        } catch (Exception e) {
            OpisMod.LOGGER.warn(String.format("Error while profiling packet %s\n", msg));
        }
    }
}
