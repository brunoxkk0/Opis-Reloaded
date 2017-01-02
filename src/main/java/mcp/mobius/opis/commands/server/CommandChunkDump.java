package mcp.mobius.opis.commands.server;

import mcp.mobius.opis.ModOpis;
import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.Level;

import java.util.*;
import net.minecraft.world.chunk.Chunk;

public class CommandChunkDump extends CommandBase {

    @Override
    public String getCommandName() {
        return "chunkdump";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        ModOpis.log.log(Level.INFO, "== CHUNK DUMP ==");

        HashMap<ChunkPos, Boolean> chunkStatus = new HashMap<>();

        Integer[] worldIDs = DimensionManager.getIDs();
        for (Integer worldID : worldIDs) {
            Set<ChunkPos> persistantChunks = DimensionManager.getWorld(worldID).getPersistentChunks().keySet();
            Collection<Chunk> chunks = DimensionManager.getWorld(worldID).getChunkProvider().getLoadedChunks();
            chunks.stream().map((chunk) -> {
                ModOpis.log.log(Level.INFO, String.format("Dim : %s, %s, Forced : %s", worldID, chunk, persistantChunks.contains(chunk.getChunkCoordIntPair())));
                return chunk;
            }).forEachOrdered((chunk) -> {
                chunkStatus.put(chunk.getChunkCoordIntPair(), persistantChunks.contains(chunk.getChunkCoordIntPair()));
            });
        }

        //((EntityPlayerMP)icommandsender).playerNetServerHandler.sendPacketToPlayer(Packet_LoadedChunks.create(chunkStatus));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return false;
    }

}
