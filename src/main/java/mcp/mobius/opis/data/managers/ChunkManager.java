package mcp.mobius.opis.data.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import mcp.mobius.opis.data.profilers.ProfilerSection;
import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.basetypes.TicketData;
import mcp.mobius.opis.data.holders.newtypes.DataEntity;
import mcp.mobius.opis.data.holders.newtypes.DataBlockTileEntity;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.data.profilers.ProfilerEntityUpdate;
import mcp.mobius.opis.data.profilers.ProfilerTileEntityUpdate;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.enums.Message;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public enum ChunkManager implements IMessageHandler {
    INSTANCE;

    private final ArrayList<CoordinatesChunk> chunksLoad = new ArrayList<>();
    private final HashMap<CoordinatesChunk, StatsChunk> chunkMeanTime = new HashMap<>();
    public ArrayList<TicketData> tickets = new ArrayList<>();

    public void addLoadedChunks(ArrayList<ISerializable> data) {
        //chunksLoad.clear();
        data.forEach((chunk) -> {
            chunksLoad.add((CoordinatesChunk) chunk);
        });
    }

    public ArrayList<CoordinatesChunk> getLoadedChunks() {
        return chunksLoad;
    }

    public void setChunkMeanTime(ArrayList<ISerializable> data) {
        chunkMeanTime.clear();
        data.forEach((stat) -> {
            chunkMeanTime.put(((StatsChunk) stat).getChunk(), (StatsChunk) stat);
        });
    }

    public HashMap<CoordinatesChunk, StatsChunk> getChunkMeanTime() {
        return chunkMeanTime;
    }

    public ArrayList<CoordinatesChunk> getLoadedChunks(int dimension) {
        HashSet<CoordinatesChunk> chunkStatus = new HashSet<>();
        WorldServer world = DimensionManager.getWorld(dimension);
        if (world != null) {
            world.getPersistentChunks().keySet().forEach((coord) -> {
                chunkStatus.add(new CoordinatesChunk(dimension, coord, (byte) 1));
            });
            world.getChunkProvider().getLoadedChunks().forEach((chunk) -> {
                chunkStatus.add(new CoordinatesChunk(dimension, chunk.getChunkCoordIntPair(), (byte)0));
            });
        }

        return new ArrayList<>(chunkStatus);
    }

    public HashSet<TicketData> getTickets() {
        HashSet<TicketData> tickets = new HashSet<>();
        for (int dim : DimensionManager.getIDs()) {
            DimensionManager.getWorld(dim).getPersistentChunks().values().forEach((ticket) -> {
                tickets.add(new TicketData(ticket));
            });
        }

        return tickets;
    }

    public ArrayList<StatsChunk> getChunksUpdateTime() {
        HashMap<CoordinatesChunk, StatsChunk> chunks = new HashMap<>();

        ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data.keySet().stream().map((coords) -> new DataBlockTileEntity().fill(coords)).forEachOrdered((data) -> {
            CoordinatesChunk chunk = data.pos.asCoordinatesChunk();

            if (!chunks.containsKey(chunk)) {
                chunks.put(chunk, new StatsChunk(chunk));
            }

            chunks.get(chunk).addTileEntity();
            chunks.get(chunk).addMeasure(data.update.timing);
        });

        ((ProfilerEntityUpdate) ProfilerSection.ENTITY_UPDATETIME.getProfiler()).data.keySet().stream().map((entity) -> new DataEntity().fill(entity)).forEachOrdered((data) -> {
            CoordinatesChunk chunk = data.pos.asCoordinatesChunk();

            if (!chunks.containsKey(chunk)) {
                chunks.put(chunk, new StatsChunk(chunk));
            }

            chunks.get(chunk).addEntity();
            chunks.get(chunk).addMeasure(data.update.timing);
        });

        ArrayList<StatsChunk> chunksUpdate = new ArrayList<>(chunks.values());
        return chunksUpdate;
    }

    public ArrayList<StatsChunk> getTopChunks(int quantity) {
        ArrayList<StatsChunk> chunks = this.getChunksUpdateTime();
        ArrayList<StatsChunk> outList = new ArrayList<>();
        Collections.sort(chunks);

        for (int i = 0; i < Math.min(quantity, chunks.size()); i++) {
            outList.add(chunks.get(i));
        }

        return outList;
    }

    public int getLoadedChunkAmount() {
        int loadedChunks = 0;
        for (WorldServer world : DimensionManager.getWorlds()) {
            int loadedChunksForDim = world.getChunkProvider().getLoadedChunkCount();
            loadedChunks += loadedChunksForDim;
            System.out.printf("[ %2d ]  %d chunks\n", world.provider.getDimension(), loadedChunksForDim);
        }
        System.out.printf("Total : %d chunks\n", loadedChunks);
        return loadedChunks;
    }

    public int getForcedChunkAmount() {
        int forcedChunks = 0;
        for (WorldServer world : DimensionManager.getWorlds()) {
            forcedChunks += world.getPersistentChunks().size();
        }
        return forcedChunks;
    }

    public void purgeChunks(int dim) {
        WorldServer world = DimensionManager.getWorld(dim);
        if (world == null) {
            return;
        }

        int loadedChunksDelta = 100;

        world.getChunkProvider().unloadAllChunks();

        while (loadedChunksDelta >= 100) {
            int loadedBefore = world.getChunkProvider().getLoadedChunkCount();
            world.getChunkProvider().unloadQueuedChunks();
            loadedChunksDelta = loadedBefore - world.getChunkProvider().getLoadedChunkCount();
        }
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case LIST_TIMING_CHUNK: {
                this.setChunkMeanTime(rawdata.array);
                break;
            }
            case LIST_CHUNK_LOADED: {
                this.addLoadedChunks(rawdata.array);
                break;
            }
            case LIST_CHUNK_LOADED_CLEAR: {
                chunksLoad.clear();
                break;
            }
            default:
                return false;
        }

        return true;
    }
}
