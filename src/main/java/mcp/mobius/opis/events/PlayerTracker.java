package mcp.mobius.opis.events;

import java.util.HashMap;
import java.util.HashSet;

import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.UUID;

import mcp.mobius.opis.OpisMod;
import mcp.mobius.opis.data.holders.basetypes.SerialLong;
import mcp.mobius.opis.data.managers.StringCache;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.AccessLevel;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataValue;
import mcp.mobius.opis.swing.SelectedTab;
import mcp.mobius.opis.swing.SwingUI;
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
        return this.getPlayerAccessLevel(player.getGameProfile().getId());
    }

    public AccessLevel getPlayerAccessLevel(UUID uuid) {
        Optional<EntityPlayerMP> player = Optional.ofNullable(FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getPlayerByUUID(uuid));
        if (player.isPresent()) {
            GameProfile profile = player.get().getGameProfile();
            if (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().canSendCommands(profile) || FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()) {
                return AccessLevel.ADMIN;
            }
        } else if (playerPrivileged.contains(uuid.toString())) {
            return AccessLevel.PRIVILEGED;
        }
        return AccessLevel.NONE;
    }

    public void addPrivilegedPlayer(UUID uuid, boolean save) {
        this.playerPrivileged.add(uuid.toString());
        if (save) {
            OpisMod.instance.config.get("ACCESS_RIGHTS", "privileged", new String[]{}, OpisMod.commentPrivileged).set(playerPrivileged.toArray(new String[]{}));
            OpisMod.instance.config.save();
        }
    }

    public void addPrivilegedPlayer(UUID uuid) {
        this.addPrivilegedPlayer(uuid, true);
    }

    public void rmPrivilegedPlayer(UUID uuid) {
        this.playerPrivileged.remove(uuid.toString());
        OpisMod.instance.config.get("ACCESS_RIGHTS", "privileged", new String[]{}, OpisMod.commentPrivileged).set(playerPrivileged.toArray(new String[]{}));
        OpisMod.instance.config.save();
    }

    public void reloeadPriviligedPlayers() {
        String[] users = OpisMod.instance.config.get("ACCESS_RIGHTS", "privileged", new String[]{}, OpisMod.commentPrivileged).getStringList();
        for (String user : users) {
            PlayerTracker.INSTANCE.addPrivilegedPlayer(UUID.fromString(user), false);
        }
    }

    public boolean isAdmin(EntityPlayerMP player) {
        return this.getPlayerAccessLevel(player).ordinal() >= AccessLevel.ADMIN.ordinal();
    }

    public boolean isAdmin(UUID uuid) {
        return this.getPlayerAccessLevel(uuid).ordinal() >= AccessLevel.ADMIN.ordinal();
    }

    public boolean isPrivileged(EntityPlayerMP player) {
        return this.getPlayerAccessLevel(player).ordinal() >= AccessLevel.PRIVILEGED.ordinal();
    }

    public boolean isPrivileged(UUID uuid) {
        return this.getPlayerAccessLevel(uuid).ordinal() >= AccessLevel.PRIVILEGED.ordinal();
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.playerDimension.remove((EntityPlayerMP) event.player);
            this.playersSwing.remove((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PacketManager.validateAndSend(new NetDataValue(Message.STATUS_CURRENT_TIME, new SerialLong(System.currentTimeMillis())), (EntityPlayerMP) event.player);
        StringCache.INSTANCE.syncCache((EntityPlayerMP) event.player);
    }
}
