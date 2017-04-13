package mcp.mobius.opis.network;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import mcp.mobius.opis.data.profilers.ProfilerSection;
import mcp.mobius.opis.OpisMod;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.AmountHolder;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.basetypes.SerialInt;
import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.data.holders.basetypes.SerialString;
import mcp.mobius.opis.data.holders.basetypes.TargetEntity;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTick;
import mcp.mobius.opis.data.holders.newtypes.DataChunkEntities;
import mcp.mobius.opis.data.holders.newtypes.DataEntity;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTileEntity;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.data.managers.ChunkManager;
import mcp.mobius.opis.data.managers.EntityManager;
import mcp.mobius.opis.data.managers.TileEntityManager;
import mcp.mobius.opis.events.OpisServerTickHandler;
import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataCommand;
import mcp.mobius.opis.network.packets.server.NetDataList;
import mcp.mobius.opis.network.packets.server.NetDataValue;
import mcp.mobius.opis.swing.SelectedTab;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ServerMessageHandler {

    private static ServerMessageHandler _instance;

    private ServerMessageHandler() {
    }

    public static ServerMessageHandler instance() {
        if (_instance == null) {
            _instance = new ServerMessageHandler();
        }
        return _instance;
    }

    public void handle(Message maintype, ISerializable param1, ISerializable param2, EntityPlayerMP player) {
        String name = player.getGameProfile().getName();

        if (null == maintype) {
            OpisMod.LOGGER.log(Level.WARN, String.format("Unknown data request : %s ", maintype));
        } else {
            switch (maintype) {
                case OVERLAY_CHUNK_ENTITIES:
                    this.handleOverlayChunkEntities((CoordinatesChunk) param1, player);
                    break;
                case OVERLAY_CHUNK_TIMING: {
                    ArrayList<StatsChunk> timingChunks = ChunkManager.INSTANCE.getTopChunks(100);
                    PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_CHUNK, timingChunks), player);
                    break;
                }
                case LIST_CHUNK_TILEENTS:
                    PacketManager.validateAndSend(new NetDataList(Message.LIST_CHUNK_TILEENTS, TileEntityManager.INSTANCE.getTileEntitiesInChunk((CoordinatesChunk) param1)), player);
                    break;
                case LIST_CHUNK_ENTITIES:
                    PacketManager.validateAndSend(new NetDataList(Message.LIST_CHUNK_ENTITIES, EntityManager.INSTANCE.getEntitiesInChunk((CoordinatesChunk) param1)), player);
                    break;
                case LIST_CHUNK_LOADED:
                    PlayerTracker.INSTANCE.playerDimension.put(player, ((SerialInt) param1).value);
                    PacketManager.validateAndSend(new NetDataCommand(Message.LIST_CHUNK_LOADED_CLEAR), player);
                    PacketManager.splitAndSend(Message.LIST_CHUNK_LOADED, ChunkManager.INSTANCE.getLoadedChunks(((SerialInt) param1).value), player);
                    break;
                case LIST_CHUNK_TICKETS:
                    //PacketManager.sendToPlayer(new PacketTickets(ChunkManager.INSTANCE.getTickets()), player);
                    PacketManager.validateAndSend(new NetDataList(Message.LIST_CHUNK_TICKETS, new ArrayList<>(ChunkManager.INSTANCE.getTickets())), player);
                    break;
                case LIST_TIMING_TILEENTS: {
                    ArrayList<DataBlockTileEntity> timingTileEnts = TileEntityManager.INSTANCE.getWorses(100);
                    DataTiming totalTime = TileEntityManager.INSTANCE.getTotalUpdateTime();
                    PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_TILEENTS, timingTileEnts), player);
                    PacketManager.validateAndSend(new NetDataValue(Message.VALUE_TIMING_TILEENTS, totalTime), player);
                    break;
                }
                case LIST_TIMING_ENTITIES: {
                    ArrayList<DataEntity> timingEntities = EntityManager.INSTANCE.getWorses(100);
                    DataTiming totalTime = EntityManager.INSTANCE.getTotalUpdateTime();
                    PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_ENTITIES, timingEntities), player);
                    PacketManager.validateAndSend(new NetDataValue(Message.VALUE_TIMING_ENTITIES, totalTime), player);
                    break;
                }
                //ArrayList<DataHandler> timingHandlers = TickHandlerManager.getCumulatedStatsServer();
                //DataTiming totalTime = TickHandlerManager.getTotalUpdateTime();
                //OpisPacketHandler_OLD.validateAndSend(NetDataList_OLD.create(Message.LIST_TIMING_HANDLERS,  timingHandlers), player);
                //OpisPacketHandler_OLD.validateAndSend(NetDataValue_OLD.create(Message.VALUE_TIMING_HANDLERS, totalTime),     player);
                case LIST_TIMING_HANDLERS:
                    break;
                case LIST_TIMING_CHUNK: {
                    ArrayList<StatsChunk> timingChunks = ChunkManager.INSTANCE.getTopChunks(100);
                    PacketManager.validateAndSend(new NetDataList(Message.LIST_TIMING_CHUNK, timingChunks), player);
                    break;
                }
                case VALUE_TIMING_WORLDTICK:
                    PacketManager.validateAndSend(new NetDataValue(Message.VALUE_TIMING_WORLDTICK, new DataBlockTick().fill()), player);
                    break;
                case VALUE_TIMING_ENTUPDATE:
                    break;
                case LIST_AMOUNT_ENTITIES:
                    boolean filtered = false;
                    if (PlayerTracker.INSTANCE.filteredAmount.containsKey(name)) {
                        filtered = PlayerTracker.INSTANCE.filteredAmount.get(name);
                    }
                    ArrayList<AmountHolder> ents = EntityManager.INSTANCE.getCumulativeEntities(filtered);
                    PacketManager.validateAndSend(new NetDataList(Message.LIST_AMOUNT_ENTITIES, ents), player);
                    break;
                case LIST_AMOUNT_TILEENTS:
                    PacketManager.validateAndSend(new NetDataList(Message.LIST_AMOUNT_TILEENTS, TileEntityManager.INSTANCE.getCumulativeAmountTileEntities()), player);
                    break;
                case COMMAND_FILTERING_TRUE:
                    PlayerTracker.INSTANCE.filteredAmount.put(name, true);
                    break;
                case COMMAND_FILTERING_FALSE:
                    PlayerTracker.INSTANCE.filteredAmount.put(name, false);
                    break;
                case COMMAND_UNREGISTER:
                    PlayerTracker.INSTANCE.playerDimension.remove(player);
                    break;
                case COMMAND_START:
                    OpisMod.profilerRun = false;
                    OpisMod.selectedBlock = null;
                    OpisServerTickHandler.INSTANCE.profilerRunningTicks = 0;

                    ProfilerSection.resetAll(Side.SERVER);
                    ProfilerSection.desactivateAll(Side.SERVER);
                    //ProfilerSection.resetAll(Side.CLIENT);
                    //ProfilerSection.desactivateAll(Side.CLIENT);	
                    OpisMod.profilerRun = true;
                    ProfilerSection.activateAll(Side.SERVER);
                    PacketManager.sendPacketToAllSwing(new NetDataValue(Message.STATUS_START, new SerialInt(OpisMod.profilerMaxTicks)));
                    break;
                case COMMAND_KILLALL:
                    EntityManager.INSTANCE.killAll(((SerialString) param1).value);
                    //this.handle(Message.LIST_AMOUNT_ENTITIES, null, null, player);
                    break;
                case COMMAND_UNREGISTER_SWING:
                    PlayerTracker.INSTANCE.playersSwing.remove(player);
                    break;
                case STATUS_TIME_LAST_RUN:
                    PacketManager.validateAndSend(new NetDataValue(Message.STATUS_TIME_LAST_RUN, new SerialLong(ProfilerSection.timeStampLastRun)), player);
                    break;
                case COMMAND_KILL_HOSTILES_ALL:
                    for (int dim : DimensionManager.getIDs()) {
                        EntityManager.INSTANCE.killAllPerClass(dim, EntityMob.class);
                    }
                    break;
                case COMMAND_KILL_HOSTILES_DIM:
                    EntityManager.INSTANCE.killAllPerClass(((SerialInt) param1).value, EntityMob.class);
                    break;
                case COMMAND_KILL_STACKS_ALL:
                    for (int dim : DimensionManager.getIDs()) {
                        EntityManager.INSTANCE.killAllPerClass(dim, EntityItem.class);
                    }
                    break;
                case COMMAND_KILL_STACKS_DIM:
                    EntityManager.INSTANCE.killAllPerClass(((SerialInt) param1).value, EntityItem.class);
                    break;
                case COMMAND_PURGE_CHUNKS_ALL:
                    for (int dim : DimensionManager.getIDs()) {
                        ChunkManager.INSTANCE.purgeChunks(dim);
                    }
                    break;

                case COMMAND_TELEPORT_BLOCK:
                    EntityManager.INSTANCE.teleportPlayer(player, (CoordinatesBlock) param1);
                    PacketManager.validateAndSend(new NetDataValue(Message.CLIENT_HIGHLIGHT_BLOCK, param1), player);
                    break;

                case COMMAND_TELEPORT_TO_ENTITY:
                    EntityManager.INSTANCE.teleportEntity(player, EntityManager.INSTANCE.getEntity(((TargetEntity) param1).entityID, ((TargetEntity) param1).dim), player);
                    break;

                case COMMAND_TELEPORT_PULL_ENTITY:
                    EntityManager.INSTANCE.teleportEntity(EntityManager.INSTANCE.getEntity(((TargetEntity)param1).entityID, ((TargetEntity)param1).dim), player, player);
                    break;
                case COMMAND_TELEPORT_CHUNK:
                    CoordinatesChunk chunkCoord = (CoordinatesChunk) param1;
                    World world = DimensionManager.getWorld(chunkCoord.dim);
                    if (world == null) {
                        return;
                    }

                    BlockPos pos = new BlockPos(chunkCoord.x + 8, chunkCoord.y, chunkCoord.z + 8);
                    BlockPos top = world.getTopSolidOrLiquidBlock(pos);
                    CoordinatesBlock blockCoord = new CoordinatesBlock(chunkCoord.dim, top.getX(), top.getY(), top.getZ());

                    EntityManager.INSTANCE.teleportPlayer(player, blockCoord);
                    break;
                case COMMAND_PURGE_CHUNKS_DIM:
                    ChunkManager.INSTANCE.purgeChunks(((SerialInt) param1).value);
                    break;
                case STATUS_PING:
                    PacketManager.validateAndSend(new NetDataValue(Message.STATUS_PING, param1), player);
                    break;
                case SWING_TAB_CHANGED:
                    SelectedTab tab = SelectedTab.values()[((SerialInt) param1).value];
                    PlayerTracker.INSTANCE.playerTab.put(player, tab);
                    break;
                case LIST_ORPHAN_TILEENTS:
                    PacketManager.validateAndSend(new NetDataCommand(Message.LIST_ORPHAN_TILEENTS_CLEAR), player);
                    PacketManager.splitAndSend(Message.LIST_ORPHAN_TILEENTS, TileEntityManager.INSTANCE.getOrphans(), player);
                    break;
                default:
                    OpisMod.LOGGER.log(Level.WARN, String.format("Unknown data request : %s ", maintype));
                    break;
            }
        }
    }

    public void handleOverlayChunkEntities(CoordinatesChunk coord, EntityPlayerMP player) {

        HashMap<CoordinatesChunk, ArrayList<DataEntity>> entities = EntityManager.INSTANCE.getAllEntitiesPerChunk();
        //HashMap<CoordinatesChunk, Integer> perChunk = new HashMap<CoordinatesChunk, Integer>();
        ArrayList<DataChunkEntities> perChunk = new ArrayList<>();

        entities.keySet().forEach((chunk) -> {
            perChunk.add(new DataChunkEntities(chunk, entities.get(chunk).size()));
        });

        PacketManager.validateAndSend(new NetDataList(Message.OVERLAY_CHUNK_ENTITIES, perChunk), player);
    }

}
