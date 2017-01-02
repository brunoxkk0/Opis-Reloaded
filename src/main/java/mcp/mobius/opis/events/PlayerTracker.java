package mcp.mobius.opis.events;

import java.util.HashMap;
import java.util.HashSet;

import com.mojang.authlib.GameProfile;

import mcp.mobius.opis.ModOpis;
import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.data.managers.StringCache;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataValue;
import mcp.mobius.opis.swing.SelectedTab;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public enum PlayerTracker {
    INSTANCE;

    private PlayerTracker() {
    }

    public HashSet<EntityPlayerMP> playersSwing = new HashSet<>(); //This is the list of players who have opened the UI
    public HashMap<String, Boolean> filteredAmount = new HashMap<>(); //Should the entity amount be filtered or not
    public HashMap<EntityPlayerMP, Integer> playerDimension = new HashMap<>();
    public HashMap<EntityPlayerMP, SelectedTab> playerTab = new HashMap<>();
    private final HashSet<String> playerPrivileged = new HashSet<>();

    public SelectedTab getPlayerSelectedTab(EntityPlayerMP player) {
        return this.playerTab.get(player);
    }

    public AccessLevel getPlayerAccessLevel(EntityPlayerMP player) {
        return this.getPlayerAccessLevel(player.getGameProfile().getName());
    }

    public AccessLevel getPlayerAccessLevel(String name) {
        GameProfile profile = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getPlayerByUsername(name).getGameProfile();

        if (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().canSendCommands(profile) || FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()) {
            return AccessLevel.ADMIN;
        } else if (playerPrivileged.contains(name)) {
            return AccessLevel.PRIVILEGED;
        } else {
            return AccessLevel.NONE;
        }
    }

    public void addPrivilegedPlayer(String name, boolean save) {
        this.playerPrivileged.add(name);
        if (save) {
            ModOpis.instance.config.get("ACCESS_RIGHTS", "privileged", new String[]{}, ModOpis.commentPrivileged).set(playerPrivileged.toArray(new String[]{}));
            ModOpis.instance.config.save();
        }
    }

    public void addPrivilegedPlayer(String name) {
        this.addPrivilegedPlayer(name, true);
    }

    public void rmPrivilegedPlayer(String name) {
        this.playerPrivileged.remove(name);
        ModOpis.instance.config.get("ACCESS_RIGHTS", "privileged", new String[]{}, ModOpis.commentPrivileged).set(playerPrivileged.toArray(new String[]{}));
        ModOpis.instance.config.save();
    }

    public void reloeadPriviligedPlayers() {
        String[] users = ModOpis.instance.config.get("ACCESS_RIGHTS", "privileged", new String[]{}, ModOpis.commentPrivileged).getStringList();
        for (String s : users) {
            PlayerTracker.INSTANCE.addPrivilegedPlayer(s, false);
        }
    }

    public boolean isAdmin(EntityPlayerMP player) {
        return this.getPlayerAccessLevel(player).ordinal() >= AccessLevel.ADMIN.ordinal();
    }

    public boolean isAdmin(String name) {
        return this.getPlayerAccessLevel(name).ordinal() >= AccessLevel.ADMIN.ordinal();
    }

    public boolean isPrivileged(EntityPlayerMP player) {
        return this.getPlayerAccessLevel(player).ordinal() >= AccessLevel.PRIVILEGED.ordinal();
    }

    public boolean isPrivileged(String name) {
        return this.getPlayerAccessLevel(name).ordinal() >= AccessLevel.PRIVILEGED.ordinal();
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        this.playerDimension.remove(event.player);
        //this.playersOpis.remove(player);
        this.playersSwing.remove(event.player);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PacketManager.validateAndSend(new NetDataValue(Message.STATUS_CURRENT_TIME, new SerialLong(System.currentTimeMillis())), (EntityPlayerMP) event.player);
        StringCache.INSTANCE.syncCache((EntityPlayerMP) event.player);
    }
}
