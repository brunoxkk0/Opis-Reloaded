package mcp.mobius.opis.data.profilers;

import java.util.HashMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import net.minecraft.world.World;
import mcp.mobius.opis.data.profilers.Clock.IClock;

public class ProfilerDimTick extends ProfilerAbstract implements IProfilerBase {

    private final IClock clock = Clock.getNewClock();
    public HashMap<Integer, DescriptiveStatistics> data = new HashMap<>();

    @Override
    public void reset() {
        this.data.clear();
    }

    @Override
    public void start(Object key) {
        World world = (World) key;
        if (world.isRemote) {
            return;
        }

        if (!data.containsKey(world.provider.getDimension())) {
            data.put(world.provider.getDimension(), new DescriptiveStatistics(20));
        }
        clock.start();
    }

    @Override
    public void stop(Object key) {
        World world = (World) key;
        if (world.isRemote) {
            return;
        }

        clock.stop();
        data.get(world.provider.getDimension()).addValue(clock.getDelta());
    }
}
