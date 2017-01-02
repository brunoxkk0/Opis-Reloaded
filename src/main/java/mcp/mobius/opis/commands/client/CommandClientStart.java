package mcp.mobius.opis.commands.client;

import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataCommand;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandClientStart extends CommandBase {

    @Override
    public String getCommandName() {
        return "opiscstart";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
        if (icommandsender instanceof EntityPlayerMP) {
            PacketManager.validateAndSend(new NetDataCommand(Message.CLIENT_START_PROFILING), (EntityPlayerMP) icommandsender);
        }
        //((EntityPlayerMP)icommandsender).playerNetServerHandler.sendPacketToPlayer(NetDataCommand.create(Message.CLIENT_START_PROFILING));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

}
