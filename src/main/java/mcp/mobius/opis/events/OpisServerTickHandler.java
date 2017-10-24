package mcp.mobius.opis.events;

import java.util.ArrayList;
import java.util.HashMap;

import mcp.mobius.opis.OpisConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import mcp.mobius.opis.OpisMod;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.data.holders.newtypes.DataDimension;
import mcp.mobius.opis.data.holders.newtypes.DataThread;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import mcp.mobius.opis.data.managers.ChunkManager;
import mcp.mobius.opis.data.managers.EntityManager;
import mcp.mobius.opis.data.managers.StringCache;
import mcp.mobius.opis.data.managers.TileEntityManager;
import mcp.mobius.opis.data.profilers.ProfilerPacket;
import mcp.mobius.opis.data.profilers.ProfilerTick;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataList;
import mcp.mobius.opis.network.packets.server.NetDataValue;
import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public enum OpisServerTickHandler {
    INSTANCE;

    public long profilerUpdateTickCounter = 0;
    public int profilerRunningTicks;
    public EventTimer timer500 = new EventTimer(500);
    public EventTimer timer1000 = new EventTimer(1000);
    public EventTimer timer2000 = new EventTimer(2000);
    public EventTimer timer5000 = new EventTimer(5000);
    public EventTimer timer10000 = new EventTimer(10000);

    public HashMap<EntityPlayerMP, AccessLevel> cachedAccess = new HashMap<>();

    @SubscribeEvent
    public void tickEnd(TickEvent.ServerTickEvent event) {
        StringCache.INSTANCE.syncNewCache();

        // One second timer
        if (timer1000.isDone() && PlayerTracker.INSTANCE.playersSwing.size() > 0) {

            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_AMOUNT_DOWNLOAD, new SerialLong(((ProfilerPacket) ProfilerSection.PACKET_OUTBOUND.getProfiler()).dataAmount)));
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_AMOUNT_UPLOAD, new SerialLong(((ProfilerPacket) ProfilerSection.PACKET_INBOUND.getProfiler()).dataAmount)));
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_CHUNK_FORCED, new SerialInt(ChunkManager.INSTANCE.getForcedChunkAmount())));
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_CHUNK_LOADED, new SerialInt(ChunkManager.INSTANCE.getLoadedChunkAmount())));
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_TIMING_TICK, new DataTiming(((ProfilerTick) ProfilerSection.TICK.getProfiler()).data.getGeometricMean())));
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_AMOUNT_TILEENTS, new SerialInt(TileEntityManager.INSTANCE.getAmountTileEntities())));
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_AMOUNT_ENTITIES, new SerialInt(EntityManager.INSTANCE.getAmountEntities())));

            PlayerTracker.INSTANCE.playersSwing.stream().filter((player) -> (!cachedAccess.containsKey(player) || cachedAccess.get(player) != PlayerTracker.INSTANCE.getPlayerAccessLevel(player))).map((player) -> {
                PacketManager.validateAndSend(new NetDataValue(Message.STATUS_ACCESS_LEVEL, new SerialInt(PlayerTracker.INSTANCE.getPlayerAccessLevel(player).ordinal())), player);
                return player;
            }).forEachOrdered((player) -> {
                cachedAccess.put(player, PlayerTracker.INSTANCE.getPlayerAccessLevel(player));
            });

            ArrayList<DataThread> threads = new ArrayList<>();
            Thread.getAllStackTraces().keySet().forEach((t) -> {
                threads.add(new DataThread().fill(t));
            });
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_THREADS, threads));

            // Dimension data update.
            ArrayList<DataDimension> dimData = new ArrayList<>();
            for (int dim : DimensionManager.getIDs()) {
                dimData.add(new DataDimension().fill(dim));
            }
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_DIMENSION_DATA, dimData));

            // Profiler update (if running)
            if (OpisMod.profilerRun) {
                PacketManager.sendPacketToAllSwing(new NetDataValue(Message.STATUS_RUNNING, new SerialInt(OpisConfig.profilerMaxTicks)));
                PacketManager.sendPacketToAllSwing(new NetDataValue(Message.STATUS_RUN_UPDATE, new SerialInt(profilerRunningTicks)));
            }
            ((ProfilerPacket) ProfilerSection.PACKET_INBOUND.getProfiler()).dataAmount = 0L;
            ((ProfilerPacket) ProfilerSection.PACKET_OUTBOUND.getProfiler()).dataAmount = 0L;
        }

        // Two second timer
        if (timer2000.isDone() && PlayerTracker.INSTANCE.playersSwing.size() > 0) {
            ArrayList<DataThread> threads = new ArrayList<>();
            Thread.getAllStackTraces().keySet().forEach((t) -> {
                threads.add(new DataThread().fill(t));
            });
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_THREADS, threads));

            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PLAYERS, EntityManager.INSTANCE.getAllPlayers()));
        }

        // Five second timer
        if (timer5000.isDone() && PlayerTracker.INSTANCE.playersSwing.size() > 0) {
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_AMOUNT_ENTITIES, EntityManager.INSTANCE.getCumulativeEntities(false)));
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_AMOUNT_TILEENTS, TileEntityManager.INSTANCE.getCumuativeAmountTileEntities()));

            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_OUTBOUND, new ArrayList<>(((ProfilerPacket) ProfilerSection.PACKET_OUTBOUND.getProfiler()).data.values())));
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_INBOUND, new ArrayList<>(((ProfilerPacket) ProfilerSection.PACKET_INBOUND.getProfiler()).data.values())));

            ((ProfilerPacket) ProfilerSection.PACKET_OUTBOUND.getProfiler()).startInterval();
            ((ProfilerPacket) ProfilerSection.PACKET_INBOUND.getProfiler()).startInterval();
        }

        profilerUpdateTickCounter++;

        if (profilerRunningTicks < OpisConfig.profilerMaxTicks && OpisMod.profilerRun) {
            profilerRunningTicks++;
        } else if (profilerRunningTicks >= OpisConfig.profilerMaxTicks && OpisMod.profilerRun) {
            profilerRunningTicks = 0;
            OpisMod.profilerRun = false;
            ProfilerSection.desactivateAll(Side.SERVER);

            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.STATUS_STOP, new SerialInt(OpisConfig.profilerMaxTicks)));

            PlayerTracker.INSTANCE.playersSwing.forEach((player) -> {
                PacketManager.sendFullUpdate(player);
            });

        }
    }
}
