package mcp.mobius.opis.data.profilers;

import java.util.WeakHashMap;

import mcp.mobius.opis.OpisMod;
import mcp.mobius.opis.data.profilers.Clock.IClock;
import net.minecraft.entity.Entity;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class ProfilerRenderEntity extends ProfilerAbstract {

    public WeakHashMap<Entity, DescriptiveStatistics> data = new WeakHashMap<>();
    private final IClock clock = Clock.getNewClock();

    @Override
    public void reset() {
        data.clear();
    }

    @Override
    public void start(Object key) {
        Entity entity = (Entity) key;
        if (!data.containsKey(entity)) {
            data.put(entity, new DescriptiveStatistics());
        }

        clock.start();
    }

    @Override
    public void stop(Object key) {
        clock.stop();
        try {
            if (key instanceof Entity) {
                data.get(((Entity)key)).addValue(clock.getDelta());
            }
        } catch (Exception e) {
            OpisMod.LOGGER.warn(String.format("Error while profiling render entity %s\n", key));
        }
    }
}
