package mcp.mobius.opis.commands.client;

import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.network.PacketManager;
import mcp.mobius.opis.network.enums.Message;
import mcp.mobius.opis.network.packets.server.NetDataCommand;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandOpis extends CommandBase {

    @Override
    public String getName() {
        return "opis";
    }

    @Override
    public String getUsage(ICommandSender icommandsender) {
        return "/opis";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {
        if (!(icommandsender instanceof EntityPlayerMP)) {
            icommandsender.sendMessage(new TextComponentString("You are not a normal client and can't open the Swing interface."));
            return;
        }

        if (!Message.COMMAND_OPEN_SWING.canPlayerUseCommand((EntityPlayerMP) icommandsender)) {
            icommandsender.sendMessage(new TextComponentString("Your access level prevents you from doing that."));
            return;
        }

        PlayerTracker.INSTANCE.playersSwing.add((EntityPlayerMP) icommandsender);
        if (icommandsender instanceof EntityPlayerMP) {
            PacketManager.validateAndSend(new NetDataCommand(Message.CLIENT_SHOW_SWING), (EntityPlayerMP) icommandsender);
        }
        PacketManager.sendFullUpdate((EntityPlayerMP) icommandsender);
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
