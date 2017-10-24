package mcp.mobius.opis.commands.server;

import java.util.Optional;
import mcp.mobius.opis.events.PlayerTracker;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommandAddPrivileged extends CommandBase {

    @Override
    public String getName() {
        return "opisaddpriv";
    }

    @Override
    public String getUsage(ICommandSender icommandsender) {
        return "";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        switch (args.length) {
            case 0: {
                sender.sendMessage(new TextComponentString("You must supply the name of a player /opisaddpriv <playername>"));
                break;
            }
            case 1: {
                Optional<EntityPlayerMP> player = Optional.ofNullable(FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getPlayerByUsername(args[0]));
                if (player.isPresent()) {
                    PlayerTracker.INSTANCE.addPrivilegedPlayer(player.get().getUniqueID());
                    sender.sendMessage(new TextComponentString(String.format("Player %s added to Opis user list", args[0])));
                } else {
                    sender.sendMessage(new TextComponentString(String.format("Player %s not found.", args[0])));
                }
                break;
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if (sender instanceof DedicatedServer) {
            return true;
        }
        if (!(sender instanceof DedicatedServer) && !(sender instanceof EntityPlayerMP)) {
            return true;
        }
        return PlayerTracker.INSTANCE.isAdmin(((EntityPlayerMP) sender).getGameProfile().getId());
    }
}
