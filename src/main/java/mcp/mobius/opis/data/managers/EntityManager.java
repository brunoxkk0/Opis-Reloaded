package mcp.mobius.opis.data.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import mcp.mobius.opis.data.profilers.ProfilerSection;
import mcp.mobius.opis.data.holders.basetypes.AmountHolder;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesChunk;
import mcp.mobius.opis.data.holders.newtypes.DataEntity;
import mcp.mobius.opis.data.holders.newtypes.DataEntityPerClass;
import mcp.mobius.opis.data.holders.newtypes.DataTiming;
import mcp.mobius.opis.data.profilers.ProfilerEntityUpdate;
import mcp.mobius.opis.network.PacketManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum EntityManager {
    INSTANCE;

    public ArrayList<DataEntity> getWorses(int amount) {
        ArrayList<DataEntity> sorted = new ArrayList<>();
        ArrayList<DataEntity> topEntities = new ArrayList<>();

        ((ProfilerEntityUpdate) ProfilerSection.ENTITY_UPDATETIME.getProfiler()).data.keySet().forEach((entity) -> {
            sorted.add(new DataEntity().fill(entity));
        });

        Collections.sort(sorted);

        int i = 0;
        while (topEntities.size() < Math.min(amount, sorted.size()) && (i < sorted.size())) {
            DataEntity testats = sorted.get(i);

            if (testats.npoints < 40) {
                i++;
                continue;
            }
            if (DimensionManager.getWorld(testats.pos.dim) == null) {
                i++;
                continue;
            }

            topEntities.add(testats);
            i++;
        }
        return topEntities;
    }

    /* Returns all the entities in all dimensions (without timing data) */
    public ArrayList<DataEntity> getAllEntities() {
        ArrayList<DataEntity> entities = new ArrayList<>();
        for (int i : DimensionManager.getIDs()) {
            entities.addAll(this.getEntitiesInDim(i));
        }
        return entities;
    }

    /* Returns all the entities in the given dimension (without timing data) */
    public ArrayList<DataEntity> getEntitiesInDim(int dim) {
        ArrayList<DataEntity> entities = new ArrayList<>();

        World world = DimensionManager.getWorld(dim);
        if (world == null) {
            return entities;
        }

        ArrayList copyList = new ArrayList(world.loadedEntityList);

        for (int i = 0; i < copyList.size(); i++) {
            Entity ent = (Entity) copyList.get(i);
            entities.add(new DataEntity().fill(ent));
        }

        return entities;
    }

    /* Returns a hashmap of all entities per chunk (not timing) */
    public HashMap<CoordinatesChunk, ArrayList<DataEntity>> getAllEntitiesPerChunk() {
        HashMap<CoordinatesChunk, ArrayList<DataEntity>> entities = new HashMap<>();
        for (int i : DimensionManager.getIDs()) {
            entities.putAll(this.getEntitiesPerChunkInDim(i));
        }
        return entities;
    }

    /* Returns a hashmap of entities in the given dimension (not timing) */
    public HashMap<CoordinatesChunk, ArrayList<DataEntity>> getEntitiesPerChunkInDim(int dim) {
        HashMap<CoordinatesChunk, ArrayList<DataEntity>> entities = new HashMap<>();
        World world = DimensionManager.getWorld(dim);
        if (world == null) {
            return entities;
        }

        ArrayList copyList = new ArrayList(world.loadedEntityList);

        for (int i = 0; i < copyList.size(); i++) {
            Entity ent = (Entity) copyList.get(i);
            CoordinatesChunk chunk = new CoordinatesBlock(ent.dimension, (int) ent.posX, (int) ent.posY, (int) ent.posZ).asCoordinatesChunk();

            if (!entities.containsKey(chunk)) {
                entities.put(chunk, new ArrayList<>());
            }

            //entities.get(chunk).add(new EntityStats(ent.entityId, ent.getClass().getName(), ent.dimension, ent.posX, ent.posY, ent.posZ));
            entities.get(chunk).add(new DataEntity().fill(ent));
        }

        return entities;
    }

    /* Returns an array of all entities in a given chunk */
    public ArrayList<DataEntity> getEntitiesInChunk(CoordinatesChunk coord) {
        ArrayList<DataEntity> entities = new ArrayList<>();

        World world = DimensionManager.getWorld(coord.dim);
        if (world == null) {
            return entities;
        }

        ArrayList copyList = new ArrayList(world.loadedEntityList);

        for (int i = 0; i < copyList.size(); i++) {
            Entity ent = (Entity) copyList.get(i);
            CoordinatesChunk chunk = new CoordinatesBlock(ent.dimension, (int) ent.posX, (int) ent.posY, (int) ent.posZ).asCoordinatesChunk();
            if (chunk.equals(coord)) //entities.add(new EntityStats(ent.entityId, ent.getClass().getName(), ent.dimension, ent.posX, ent.posY, ent.posZ));
            {
                entities.add(new DataEntity().fill(ent));
            }
        }

        return entities;
    }

    /* Returns a hashmap with the entity name and amount of it on the server */
    public ArrayList<AmountHolder> getCumulativeEntities(boolean filtered) {
        ArrayList<AmountHolder> cumData = new ArrayList<>();
        HashMap<String, Integer> entities = new HashMap<>();

        for (int dim : DimensionManager.getIDs()) {
            World world = DimensionManager.getWorld(dim);
            if (world == null) {
                continue;
            }

            ArrayList copyList = new ArrayList(world.loadedEntityList);

            for (int i = 0; i < copyList.size(); i++) {
                Entity ent = (Entity) copyList.get(i);
                //String name = ent.getClass().getName();
                String name = getEntityName(ent, filtered);

                if (!entities.containsKey(name)) {
                    entities.put(name, 0);
                }

                entities.put(name, entities.get(name) + 1);
            }
        }

        entities.keySet().forEach((key) -> {
            cumData.add(new AmountHolder(key, entities.get(key)));
        });

        return cumData;
    }

    @SideOnly(value = Side.SERVER)
    public boolean teleportPlayer(EntityPlayerMP player, CoordinatesBlock target) {
        if (player.world.provider.getDimension() != target.dim) {
            PacketManager.sendChatMsg("\u00A7oCannot teleport to a location in another world.", player);
            return false;
        }
        player.setPositionAndUpdate(target.x + 1.5, target.y, target.z + 1.5);
        return true;
    }

    @SideOnly(value = Side.SERVER)
    public boolean teleportEntity(Entity src, Entity trg, EntityPlayerMP msgtrg) {
        if ((src == null) && (msgtrg != null)) {
            PacketManager.sendChatMsg(String.format("\u00A7oCannot find source entity %s", src), msgtrg);
            return false;
        }
        if ((trg == null) && (msgtrg != null)) {
            PacketManager.sendChatMsg(String.format("\u00A7oCannot find target entity %s", src), msgtrg);
            return false;
        }
        if (src != null && trg != null) {
            if (src.world.provider.getDimension() != trg.world.provider.getDimension()) {
                PacketManager.sendChatMsg("\u00A7oCannot teleport to a location in another world.", msgtrg);
                return false;
            }
            src.setLocationAndAngles(trg.posX, trg.posY, trg.posZ, src.rotationYaw, src.rotationPitch);
            return true;
        }
        return false;
    }

    public Entity getEntity(int eid, int dim) {
        World world = DimensionManager.getWorld(dim);
        if (world == null) {
            return null;
        }

        Entity entity = world.getEntityByID(eid);
        return entity;
    }

    public String getEntityName(Entity ent) {
        return getEntityName(ent, false);
    }

    public String getEntityName(Entity ent, boolean filtered) {
        if (ent instanceof EntityItem && filtered) {
            return "Dropped Item";
        } else if (ent instanceof EntityItem && !filtered) {
            try {
                return "[Stack] " + ((EntityItem) ent).getItem().getDisplayName();
            } catch (Exception e) {
                return "<Unknown dropped item>";
            }
        }

        if (ent instanceof EntityPlayerMP && filtered) {
            return "Player";
        } else if (ent instanceof EntityPlayerMP && !filtered) {
            return "[ Player ] " + ((EntityPlayerMP) ent).getGameProfile().getName();
        }

        String name = ent.getName();

        if (name.contains(".")) {
            String[] namelst = ent.getClass().getName().split("\\.");
            return namelst[namelst.length - 1];
        }

        return name;
    }

    public DataTiming getTotalUpdateTime() {
        double updateTime = 0D;
        updateTime = ((ProfilerEntityUpdate) ProfilerSection.ENTITY_UPDATETIME.getProfiler()).data.keySet().stream().map((entity) -> ((ProfilerEntityUpdate) ProfilerSection.ENTITY_UPDATETIME.getProfiler()).data.get(entity).getGeometricMean()).reduce(updateTime, (accumulator, _item) -> accumulator + _item);
        return new DataTiming(updateTime);
    }

    public int killAll(String entName) {
        int nkilled = 0;

        if (entName.contains("Player")) {
            return -1; //Error msg for when trying to kill a player
        }

        for (int dim : DimensionManager.getIDs()) {
            World world = DimensionManager.getWorld(dim);
            if (world == null) {
                continue;
            }

            ArrayList<Entity> copyList = new ArrayList(world.loadedEntityList);

            for (Entity entity : copyList) {
                if (entity != null) {
                    String nameFiltered = getEntityName(entity, true).toLowerCase();
                    String nameUnfiltered = getEntityName(entity, false).toLowerCase();

                    if (nameFiltered.equals(entName.toLowerCase()) || nameUnfiltered.equals(entName.toLowerCase())) {
                        entity.setDead();
                        nkilled += 1;
                    }
                }
            }
        }

        System.out.printf("Killed %d %s\n", nkilled, entName);

        return nkilled;
    }

    public ArrayList<DataEntity> getAllPlayers() {
        List players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();

        ArrayList<DataEntity> outList = new ArrayList<>();

        players.forEach((p) -> {
            outList.add(new DataEntity().fill((EntityPlayer) p));
        });

        return outList;
    }

    public int getAmountEntities() {
        int amountEntities = 0;
        for (WorldServer world : DimensionManager.getWorlds()) {
            amountEntities += world.loadedEntityList.size();
        }
        return amountEntities;
    }

    public int killAllPerClass(int dim, Class clazz) {
        WorldServer world = DimensionManager.getWorld(dim);
        if (world == null) {
            return -1;
        }

        int killedEnts = 0;

        ArrayList<Entity> copyList = new ArrayList(world.loadedEntityList);

        killedEnts = copyList.stream().filter((entity) -> (clazz.isInstance(entity))).map((entity) -> {
            entity.setDead();
            return entity;
        }).map((_item) -> 1).reduce(killedEnts, Integer::sum);
        return killedEnts;
    }

    public ArrayList<DataEntityPerClass> getTotalPerClass() {
        HashMap<String, DataEntityPerClass> data = new HashMap<>();

        ((ProfilerEntityUpdate) ProfilerSection.ENTITY_UPDATETIME.getProfiler()).data.keySet().forEach((entity) -> {
            String name = this.getEntityName(entity, true);
            if (!data.containsKey(name)) {
                data.put(name, new DataEntityPerClass(name));
            }

            data.get(name).add(((ProfilerEntityUpdate) ProfilerSection.ENTITY_UPDATETIME.getProfiler()).data.get(entity).getGeometricMean());
        });
        return new ArrayList<>(data.values());
    }
}
