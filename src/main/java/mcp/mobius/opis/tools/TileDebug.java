package mcp.mobius.opis.tools;

import mcp.mobius.opis.ModOpis;
import net.minecraft.tileentity.TileEntity;

public class TileDebug extends TileEntity {

    public TileDebug() {
        ModOpis.log.info("=============================");
        ModOpis.log.info(String.format("Hello, I will be your companion debug block for the day. My ID is 0x%s", System.identityHashCode(this)));
        ModOpis.log.info("For a starter, let me show you who created me :");
        StackTraceElement[] cause = Thread.currentThread().getStackTrace();

        for (StackTraceElement elem : cause) {
            ModOpis.log.info(String.format("%s.%s:%s", elem.getClassName(), elem.getMethodName(), elem.getLineNumber()));
        }
        ModOpis.log.info("=============================");

    }

}
