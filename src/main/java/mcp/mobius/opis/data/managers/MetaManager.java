package mcp.mobius.opis.data.managers;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import mcp.mobius.opis.OpisMod;
import mcp.mobius.opis.events.OpisServerTickHandler;
import net.minecraftforge.fml.relauncher.Side;

public class MetaManager {

    public static void reset() {
        OpisMod.profilerRun = false;
        OpisMod.selectedBlock = null;
        OpisServerTickHandler.INSTANCE.profilerRunningTicks = 0;

        ProfilerSection.resetAll(Side.SERVER);
        ProfilerSection.desactivateAll(Side.SERVER);
        ProfilerSection.resetAll(Side.CLIENT);
        ProfilerSection.desactivateAll(Side.CLIENT);		
    }

}
