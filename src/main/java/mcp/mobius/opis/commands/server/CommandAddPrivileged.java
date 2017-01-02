package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.events.PlayerTracker;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.TextComponentString;

public class CommandAddPrivileged extends CommandBase {

    @Override
    public String getCommandName() {
        return "opisaddpriv";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender icommandsender, String[] astring) throws CommandException {
        PlayerTracker.INSTANCE.addPrivilegedPlayer(astring[0]);
        icommandsender.addChatMessage(new TextComponentString(String.format("Player %s added to Opis user list", astring[0])));
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
