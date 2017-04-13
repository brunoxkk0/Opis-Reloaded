package mcp.mobius.opis.data.profilers;

import java.util.HashMap;
import mcp.mobius.opis.OpisMod;

import net.minecraftforge.common.DimensionManager;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import mcp.mobius.opis.data.profilers.Clock.IClock;

public class ProfilerDimBlockTick extends ProfilerAbstract implements IProfilerBase {

    private final IClock clock = Clock.getNewClock();
    public HashMap<Integer, DescriptiveStatistics> data = new HashMap<>();

    @Override
    public void reset() {
        this.data.clear();
    }

    @Override
    public void start(Object key) {
        Integer dim = (Integer) key;
        if (DimensionManager.getWorld(dim).isRemote) {
            return;
        }

        if (!data.containsKey(dim)) {
            data.put(dim, new DescriptiveStatistics());
        }
        clock.start();
    }

    @Override
    public void stop(Object key) {
        Integer dim = (Integer) key;
        if (DimensionManager.getWorld(dim).isRemote) {
            return;
        }

        clock.stop();
        try {
            data.get(dim).addValue(clock.getDelta());
        } catch (Exception e) {
            OpisMod.LOGGER.warn(String.format("Error while profiling dimension block tick %s\n", key));
        }
    }
}
