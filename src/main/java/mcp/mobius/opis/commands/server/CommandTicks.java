package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.ModOpis;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.TextComponentString;

public class CommandTicks extends CommandBase {

    @Override
    public String getCommandName() {
        return "opisticks";
    }
    
    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender icommandsender, String[] astring) throws CommandException {
        if (astring.length < 1) {
            return;
        }
        try {
            ModOpis.profilerMaxTicks = Integer.valueOf(astring[0]);
            icommandsender.addChatMessage(new TextComponentString(String.format("\u00A7oOpis ticks set to %s ticks.", astring[0])));

        } catch (NumberFormatException e) {
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
        return PlayerTracker.INSTANCE.isPrivileged(((EntityPlayerMP) sender).getGameProfile().getName());
    }

}
