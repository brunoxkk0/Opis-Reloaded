package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.events.PlayerTracker;
import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class CommandKill extends CommandBase {

    @Override
    public String getCommandName() {
        return "opiskill";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender icommandsender, String[] astring) throws CommandException {
        if (astring.length != 2) {
            return;
        }
        int dim = Integer.valueOf(astring[0]);
        int eid = Integer.valueOf(astring[1]);

        World world = DimensionManager.getWorld(dim);
        if (world == null) {
            icommandsender.addChatMessage(new TextComponentString(String.format("\u00A7oCannot find dim %d in world %d", dim)));
            return;
        }

        Entity entity = world.getEntityByID(eid);
        if (entity == null) {
            icommandsender.addChatMessage(new TextComponentString(String.format("\u00A7oCannot find entity %d in dim %d", eid, dim)));
            return;
        }

        entity.setDead();
        icommandsender.addChatMessage(new TextComponentString(String.format("\u00A7oKilled entity %d in dim %d", eid, dim)));
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
