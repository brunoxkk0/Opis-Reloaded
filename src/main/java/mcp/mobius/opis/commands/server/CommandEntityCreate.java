package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.events.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.TextComponentString;

public class CommandEntityCreate extends CommandBase {

    @Override
    public String getName() {
        return "opisenttrace";
    }

    @Override
    public String getUsage(ICommandSender icommandsender) {
        return "/opisenttrace ?<full>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender icommandsender, String[] astring) throws CommandException {
        if (astring.length == 1 && astring[0].equals("full")) {
            OpisServerEventHandler.printEntityFull = true;
            OpisServerEventHandler.printEntityTrace = true;
        } else {
            OpisServerEventHandler.printEntityTrace = !OpisServerEventHandler.printEntityTrace;
            OpisServerEventHandler.printEntityFull = false;
        }

        icommandsender.sendMessage(new TextComponentString(String.format("Entity trace is %s", OpisServerEventHandler.printEntityTrace)));
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
