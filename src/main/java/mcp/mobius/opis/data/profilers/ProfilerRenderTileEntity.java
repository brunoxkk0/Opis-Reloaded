package mcp.mobius.opis.data.profilers;

import java.util.WeakHashMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import mcp.mobius.opis.ModOpis;
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
            data.get(key).addValue(clock.getDelta());
        } catch (Exception e) {
            ModOpis.log.warn(String.format("Error while profiling entity %s\n", key));
        }
    }
}
