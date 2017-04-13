package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.OpisMod;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.TextComponentString;

public class CommandFrequency extends CommandBase {

    @Override
    public String getName() {
        return "opisdelay";
    }

    @Override
    public String getUsage(ICommandSender icommandsender) {
        return "/opisdelay <delay>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender icommandsender, String[] astring) throws CommandException {
        if (astring.length < 1) {
            return;
        }
        try {
            OpisMod.profilerDelay = Integer.valueOf(astring[0]);
            icommandsender.sendMessage(new TextComponentString(String.format("\u00A7oOpis delay set to %s ticks.", astring[0])));

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
        if (!(sender instanceof DedicatedServer) && !(sender instanceof EntityPlayerMP)) {
            return true;
        }
        return PlayerTracker.INSTANCE.isPrivileged(((EntityPlayerMP) sender).getGameProfile().getId());
    }

}
