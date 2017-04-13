package mcp.mobius.opis.tools;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileLag extends TileEntity implements ITickable {

    @Override
    public void update() {
        if (this.getWorld().isRemote) {
            return;
        }

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
