package mcp.mobius.opis.events;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import mcp.mobius.opis.ModOpis;
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

        /*
			if (System.nanoTime() - timer500 >  500000000){
				timer500 = System.nanoTime();
				
				for (Player player : PlayerTracker.instance().playersSwing){
					PacketDispatcher.sendPacketToPlayer(Packet_DataValue.create(DataReq.VALUE_TIMING_TICK,     TickProfiler.instance().stats), player);					
				}
			}
         */
        StringCache.INSTANCE.syncNewCache();

        // One second timer
        if (timer1000.isDone()) {

            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_AMOUNT_UPLOAD, new SerialLong(((ProfilerPacket) ProfilerSection.PACKET_OUTBOUND.getProfiler()).dataAmount)));
            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.VALUE_AMOUNT_DOWNLOAD, new SerialLong(((ProfilerPacket) ProfilerSection.PACKET_INBOUND.getProfiler()).dataAmount)));
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
            if (ModOpis.profilerRun) {
                PacketManager.sendPacketToAllSwing(new NetDataValue(Message.STATUS_RUNNING, new SerialInt(ModOpis.profilerMaxTicks)));
                PacketManager.sendPacketToAllSwing(new NetDataValue(Message.STATUS_RUN_UPDATE, new SerialInt(profilerRunningTicks)));
            }

            ((ProfilerPacket) ProfilerSection.PACKET_INBOUND.getProfiler()).dataAmount = 0L;
            ((ProfilerPacket) ProfilerSection.PACKET_OUTBOUND.getProfiler()).dataAmount = 0L;
        }

        // Two second timer
        if (timer2000.isDone()) {
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PLAYERS, EntityManager.INSTANCE.getAllPlayers()));
        }

        // Five second timer
        if (timer5000.isDone()) {

            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_OUTBOUND, new ArrayList<>(((ProfilerPacket) ProfilerSection.PACKET_OUTBOUND.getProfiler()).data.values())));
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_INBOUND, new ArrayList<>(((ProfilerPacket) ProfilerSection.PACKET_INBOUND.getProfiler()).data.values())));

            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_OUTBOUND_250, new ArrayList<>(((ProfilerPacket) ProfilerSection.PACKET_OUTBOUND.getProfiler()).data250.values())));
            PacketManager.sendPacketToAllSwing(new NetDataList(Message.LIST_PACKETS_INBOUND_250, new ArrayList<>(((ProfilerPacket) ProfilerSection.PACKET_INBOUND.getProfiler()).data250.values())));

            ((ProfilerPacket) ProfilerSection.PACKET_OUTBOUND.getProfiler()).startInterval();
            ((ProfilerPacket) ProfilerSection.PACKET_INBOUND.getProfiler()).startInterval();

            /*
				for (DataPacket data : ((ProfilerPacket)ProfilerSection.PACKET_OUTBOUND.getProfiler()).jabbaSpec){
					System.out.printf("[ %d ] %d %d\n", data.id, data.amount, data.size);
				}
             */
        }

        profilerUpdateTickCounter++;

        if (profilerRunningTicks < ModOpis.profilerMaxTicks && ModOpis.profilerRun) {
            profilerRunningTicks++;
        } else if (profilerRunningTicks >= ModOpis.profilerMaxTicks && ModOpis.profilerRun) {
            profilerRunningTicks = 0;
            ModOpis.profilerRun = false;
            ProfilerSection.desactivateAll(Side.SERVER);

            PacketManager.sendPacketToAllSwing(new NetDataValue(Message.STATUS_STOP, new SerialInt(ModOpis.profilerMaxTicks)));

            PlayerTracker.INSTANCE.playersSwing.forEach((player) -> {
                PacketManager.sendFullUpdate(player);
            });

        }
    }
}
