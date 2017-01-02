package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import mcp.mobius.opis.events.PlayerTracker;
import mcp.mobius.opis.ModOpis;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;

public class CommandStop extends CommandBase {

    @Override
    public String getCommandName() {
        return "opisstop";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender icommandsender, String[] args) throws CommandException {

        ModOpis.profilerRun = false;
        ProfilerSection.desactivateAll(Side.SERVER);
        icommandsender.addChatMessage(new TextComponentString(String.format("\u00A7oOpis stopped.")));
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
