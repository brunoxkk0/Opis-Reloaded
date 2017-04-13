package mcp.mobius.opis.data.profilers;

import java.util.WeakHashMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import mcp.mobius.opis.OpisMod;
import mcp.mobius.opis.data.profilers.Clock.IClock;
import net.minecraft.tileentity.TileEntity;

public class ProfilerRenderTileEntity extends ProfilerAbstract {

    public WeakHashMap<TileEntity, DescriptiveStatistics> data = new WeakHashMap<>();
    private final IClock clock = Clock.getNewClock();

    @Override
    public void reset() {
        data.clear();
    }

    @Override
    public void start(Object key) {
        TileEntity tileEnt = (TileEntity) key;
        if (!data.containsKey(tileEnt)) {
            data.put(tileEnt, new DescriptiveStatistics());
        }

        clock.start();
    }

    @Override
    public void stop(Object key) {
        clock.stop();
        try {
            if (key instanceof TileEntity) {
                data.get(((TileEntity)key)).addValue(clock.getDelta());
            }
        } catch (Exception e) {
            OpisMod.LOGGER.warn(String.format("Error while profiling render tile entity %s\n", key));
        }
    }
}
