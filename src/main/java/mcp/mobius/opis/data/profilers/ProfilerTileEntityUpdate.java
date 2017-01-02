package mcp.mobius.opis.data.profilers;

import java.util.HashMap;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.profilers.Clock.IClock;
import net.minecraft.tileentity.TileEntity;

public class ProfilerTileEntityUpdate extends ProfilerAbstract {

    private final IClock clock = Clock.getNewClock();
    public HashMap<CoordinatesBlock, DescriptiveStatistics> data = new HashMap<>();
    public HashMap<CoordinatesBlock, Class> refs = new HashMap<>();

    @Override
    public void reset() {
        this.data.clear();
        this.refs.clear();
    }

    @Override
    public void start(Object key) {
        TileEntity tileent = (TileEntity) key;
        if (tileent.getWorld().isRemote) {
            return;
        }

        CoordinatesBlock coord = new CoordinatesBlock(tileent);

        if (!data.containsKey(coord) || (refs.get(coord) != tileent.getClass())) {
            data.put(coord, new DescriptiveStatistics());
            refs.put(coord, tileent.getClass());
        }
        clock.start();
    }

    @Override
    public void stop(Object key) {
        TileEntity tileent = (TileEntity) key;
        if (tileent.getWorld().isRemote) {
            return;
        }

        clock.stop();
        CoordinatesBlock coord = new CoordinatesBlock(tileent);

        data.get(coord).addValue(clock.getDelta());
    }
}
