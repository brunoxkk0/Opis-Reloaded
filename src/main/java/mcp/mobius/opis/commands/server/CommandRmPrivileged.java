package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.events.PlayerTracker;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.TextComponentString;

public class CommandRmPrivileged extends CommandBase {

    @Override
    public String getCommandName() {
        return "opisrmpriv";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender icommandsender, String[] astring) throws CommandException {

        if (PlayerTracker.INSTANCE.isPrivileged(astring[0])) {
            PlayerTracker.INSTANCE.rmPrivilegedPlayer(astring[0]);
            icommandsender.addChatMessage(new TextComponentString(String.format("Player %s removed from Opis user list.", astring[0])));
        } else {
            icommandsender.addChatMessage(new TextComponentString(String.format("Player %s not found in list.", astring[0])));
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
        //if ((sender instanceof EntityPlayerMP) && ((EntityPlayerMP)sender).playerNetServerHandler.netManager instanceof MemoryConnection) return true;
        if (!(sender instanceof DedicatedServer) && !(sender instanceof EntityPlayerMP)) {
            return true;
        }
        return PlayerTracker.INSTANCE.isAdmin(((EntityPlayerMP) sender).getGameProfile().getName());
    }

}
