package mcp.mobius.opis.tools;

import mcp.mobius.opis.OpisMod;
import net.minecraft.tileentity.TileEntity;

public final class TileDebug extends TileEntity {

    public TileDebug() {
        info();
    }

    public void info() {
        OpisMod.LOGGER.info("=============================");
        OpisMod.LOGGER.info(String.format("Hello, I will be your companion debug block for the day. My ID is 0x%s", System.identityHashCode(this)));
        OpisMod.LOGGER.info("For a starter, let me show you who created me :");
        StackTraceElement[] cause = Thread.currentThread().getStackTrace();

        for (StackTraceElement elem : cause) {
            OpisMod.LOGGER.info(String.format("%s.%s:%s", elem.getClassName(), elem.getMethodName(), elem.getLineNumber()));
        }
        OpisMod.LOGGER.info("=============================");
    }

}
