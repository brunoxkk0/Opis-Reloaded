package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.data.managers.MetaManager;
import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataCommand;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.TextComponentString;

public class CommandReset extends CommandBase {

    @Override
    public String getName() {
        return "opisreset";
    }

    @Override
    public String getUsage(ICommandSender icommandsender) {
        return "/opisreset";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
        MetaManager.reset();

        icommandsender.sendMessage(new TextComponentString(String.format("\u00A7oInternal data reseted.")));

        if (icommandsender instanceof EntityPlayerMP) {
            PacketManager.validateAndSend(new NetDataCommand(Message.CLIENT_CLEAR_SELECTION), (EntityPlayerMP) icommandsender);
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
