package mcp.mobius.opis.data.managers;

import com.google.common.collect.HashBasedTable;
import mcp.mobius.opis.data.profilers.ProfilerSection;
import mcp.mobius.opis.data.holders.basetypes.*;
import mcp.mobius.opis.data.holders.newtypes.*;
import mcp.mobius.opis.data.holders.stats.StatsChunk;
import mcp.mobius.opis.data.profilers.ProfilerTileEntityUpdate;
import mcp.mobius.opis.OpisMod;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraftforge.common.DimensionManager;

import java.util.*;

public enum TileEntityManager {
    INSTANCE;

    public HashMap<CoordinatesChunk, StatsChunk> getTimes(int dim) {
        HashMap<CoordinatesChunk, StatsChunk> chunks = new HashMap<>();

        ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data.keySet().stream().filter((coord) -> (coord.dim == dim)).forEachOrdered((coord) -> {
            CoordinatesChunk coordC = new CoordinatesChunk(coord);
            if (!(chunks.containsKey(coordC))) {
                chunks.put(coordC, new StatsChunk());
            }

            chunks.get(coordC).addEntity();
            chunks.get(coordC).addMeasure(((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data.get(coord).getGeometricMean());
        });
        return chunks;
    }

    public ArrayList<DataBlockTileEntity> getTileEntitiesInChunk(CoordinatesChunk coord) {

        ArrayList<DataBlockTileEntity> returnList = new ArrayList<>();

        ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data.keySet().stream().filter((tecoord) -> (coord.equals(tecoord.asCoordinatesChunk()))).map((tecoord) -> new DataBlockTileEntity().fill(tecoord)).forEachOrdered((testats) -> {
            returnList.add(testats);
        });

        return returnList;
    }

    public ArrayList<DataBlockTileEntity> getWorses(int amount) {
        ArrayList<DataBlockTileEntity> sorted = new ArrayList<>();
        ArrayList<DataBlockTileEntity> topEntities = new ArrayList<>();

        ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data.keySet().forEach((coord) -> {
            sorted.add(new DataBlockTileEntity().fill(coord));
        });

        Collections.sort(sorted);

        for (int i = 0; i < Math.min(amount, sorted.size()); i++) {
            topEntities.add(sorted.get(i));
        }

        return topEntities;
    }

    public DataTiming getTotalUpdateTime() {
        double updateTime = 0D;
        updateTime = ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data.keySet().stream().map((coords) -> ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data.get(coords).getGeometricMean()).reduce(updateTime, (accumulator, _item) -> accumulator + _item);
        return new DataTiming(updateTime);
    }

    public int getAmountTileEntities() {
        int amountTileEntities = 0;
        for (WorldServer world : DimensionManager.getWorlds()) {
            amountTileEntities += world.loadedTileEntityList.size();
        }
        return amountTileEntities;
    }

    public ArrayList<DataTileEntity> getOrphans() {
        ArrayList<DataTileEntity> orphans = new ArrayList<>();
        HashMap<CoordinatesBlock, DataTileEntity> coordHashset = new HashMap<>();
        HashSet<Integer> registeredEntities = new HashSet<>();

        for (WorldServer world : DimensionManager.getWorlds()) {
                world.loadedTileEntityList.parallelStream().map((o) -> (TileEntity) o).forEachOrdered((tileEntity) -> {
                    CoordinatesBlock coord = new CoordinatesBlock(world.provider.getDimension(), tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ());
                    //This entitie has already been seen;
                    int hash = System.identityHashCode(tileEntity);
                    if (!(registeredEntities.contains(hash))) {
                        IBlockState block = world.getBlockState(tileEntity.getPos());
                        if (block != null || block.getBlock() == Blocks.AIR || !block.getBlock().hasTileEntity() || world.getTileEntity(new BlockPos(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ())) == null || world.getTileEntity(new BlockPos(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ())).getClass() != tileEntity.getClass()) {
                            orphans.add(new DataTileEntity().fill(tileEntity, "Orphan"));
                            registeredEntities.add(hash);
                        }
                        if (coordHashset.containsKey(coord)) {
                            if (!registeredEntities.contains(hash)) {
                                orphans.add(new DataTileEntity().fill(tileEntity, "Duplicate"));
                            }

                            if (!registeredEntities.contains(coordHashset.get(coord).hashCode)) {
                                orphans.add(coordHashset.get(coord));
                            }
                        }
                        if (!coordHashset.containsKey(coord)) {
                            coordHashset.put(coord, new DataTileEntity().fill(tileEntity, "Duplicate"));
                        }
                    }
                });
        }

        OpisMod.LOGGER.warn(String.format("Found %d potential orphans !", orphans.size()));

        return orphans;
    }

    public ArrayList<DataBlockTileEntityPerClass> getCumuativeAmountTileEntities() {
        HashBasedTable<Integer, Integer, DataBlockTileEntityPerClass> data = HashBasedTable.create();

        for (WorldServer world : DimensionManager.getWorlds()) {
            world.loadedTileEntityList.parallelStream().map((tile) -> world.getBlockState(tile.getPos())).filter((state) -> (state != null)).forEachOrdered((state) -> {
                Integer id = Block.getIdFromBlock(state.getBlock());
                Integer meta = state.getBlock().getMetaFromState(state);

                if (!data.contains(id, meta)) {
                    data.put(id, meta, new DataBlockTileEntityPerClass(id, meta));
                }
                data.get(id, meta).add();
            });
        }

        return new ArrayList<>(data.values());
    }

    public ArrayList<DataBlockTileEntityPerClass> getCumulativeTimingTileEntities() {
        HashBasedTable<Integer, Integer, DataBlockTileEntityPerClass> data = HashBasedTable.create();

        ((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data.keySet().forEach((coord) -> {
            World world = DimensionManager.getWorld(coord.dim);
            IBlockState state = world.getBlockState(new BlockPos(coord.x, coord.y, coord.z));
            if (state != null) {
                int id = Block.getIdFromBlock(state.getBlock());
                int meta = state.getBlock().getMetaFromState(state);

                if (!data.contains(id, meta)) {
                    data.put(id, meta, new DataBlockTileEntityPerClass(id, meta));
                }

                data.get(id, meta).add(((ProfilerTileEntityUpdate) ProfilerSection.TILEENT_UPDATETIME.getProfiler()).data.get(coord).getGeometricMean());
            }
        });

        return new ArrayList<>(data.values());
    }
}
