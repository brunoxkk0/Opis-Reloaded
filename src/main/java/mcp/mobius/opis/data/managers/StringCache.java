package mcp.mobius.opis.data.managers;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.entity.player.EntityPlayerMP;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import mcp.mobius.opis.OpisMod;

import mcp.mobius.opis.api.IMessageHandler;
import mcp.mobius.opis.api.TabPanelRegistrar;
import mcp.mobius.opis.data.holders.newtypes.DataStringUpdate;
import mcp.mobius.opis.network.PacketBase;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataList;
import mcp.mobius.opis.network.packets.server.NetDataValue;

public enum StringCache implements IMessageHandler {
    INSTANCE;

    private int currentIndex = -1;
    private final BiMap<Integer, String> cache = Maps.synchronizedBiMap(HashBiMap.<Integer, String>create());
    private final ConcurrentLinkedQueue<DataStringUpdate> fullsync = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<DataStringUpdate> unsynced = new ConcurrentLinkedQueue<>();	// This is the current list of unsynced

    public String getString(int index) {
        synchronized (cache) {
            String retVal = this.cache.get(index);
            if (retVal == null) {
                return "<ERROR>";
            } else {
                return retVal;
            }
        }
    }

    public int getIndex(String str) {
        synchronized (cache) {
            if (cache.inverse().containsKey(str)) {
                return cache.inverse().get(str);
            } else {
                currentIndex += 1;
                cache.put(currentIndex, str);

                DataStringUpdate upd = new DataStringUpdate(str, currentIndex);
                this.fullsync.add(upd);
                this.unsynced.add(upd);

                return currentIndex;
            }
        }
    }

    public void syncCache(EntityPlayerMP player) {

        int i = 0;

        ArrayList<DataStringUpdate> toSendCopy = new ArrayList(fullsync);

        while (i < toSendCopy.size()) {
            PacketManager.sendToPlayer(new NetDataList(Message.STATUS_STRINGUPD_FULL, toSendCopy.subList(i, Math.min(i + 50, toSendCopy.size()))), player);
            i += 50;
        }

    }

    public void syncNewCache() {
        while (!unsynced.isEmpty()) {
            PacketManager.sendToAll(new NetDataValue(Message.STATUS_STRINGUPD, this.unsynced.poll()));
        }
    }

    @Override
    public boolean handleMessage(Message msg, PacketBase rawdata) {
        switch (msg) {
            case STATUS_STRINGUPD: {
                OpisMod.LOGGER.debug(String.format("Received partial String Cache update : [ %d ] [ %s ]", ((DataStringUpdate)rawdata.value).index, ((DataStringUpdate)rawdata.value).str));

                DataStringUpdate data = (DataStringUpdate) rawdata.value;
                OpisMod.LOGGER.debug(String.format("++++ Received String Cache update : [%d] %s", data.index, data.str));			
                try {
                    this.cache.put(data.index, data.str);
                } catch (IllegalArgumentException e) {
                    this.cache.remove(data.index);
                    this.cache.inverse().remove(data.str);
                    this.cache.put(data.index, data.str);
                }

                TabPanelRegistrar.INSTANCE.refreshAll();

                break;
            }
            case STATUS_STRINGUPD_FULL: {
                OpisMod.LOGGER.debug(String.format("Received full String Cache update containing %d entries", rawdata.array.size()));
                rawdata.array.stream().map((idata) -> (DataStringUpdate) idata).forEachOrdered((data) -> {
                    OpisMod.LOGGER.debug(String.format("++++ Received String Cache update : [%d] %s", data.index, data.str));
                    try {
                        this.cache.put(data.index, data.str);
                    } catch (IllegalArgumentException e) {
                        this.cache.remove(data.index);
                        this.cache.inverse().remove(data.str);
                        this.cache.put(data.index, data.str);
                    }
                });

                TabPanelRegistrar.INSTANCE.refreshAll();

                break;
            }
            default:
                return false;

        }
        return true;
    }

}
