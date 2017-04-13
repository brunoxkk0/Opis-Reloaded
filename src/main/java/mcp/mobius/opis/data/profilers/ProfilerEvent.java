package mcp.mobius.opis.data.profilers;

import mcp.mobius.opis.data.profilers.Clock.IClock;

import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.collect.HashBasedTable;
import mcp.mobius.opis.OpisMod;

public class ProfilerEvent extends ProfilerAbstract {

    private final IClock clock = Clock.getNewClock();
    public HashBasedTable<Class, String, DescriptiveStatistics> data = HashBasedTable.create();
    public HashBasedTable<Class, String, String> dataMod = HashBasedTable.create();

    public HashBasedTable<Class, String, DescriptiveStatistics> dataTick = HashBasedTable.create();
    public HashBasedTable<Class, String, String> dataModTick = HashBasedTable.create();

    @Override
    public void reset() {
        data.clear();
    }

    @Override
    public void start() {
        clock.start();
    }

    @Override
    public void stop(Object event, Object handler, Object mod) {
        clock.stop();

        try {
            String eventName = event.getClass().getSimpleName();
            if (eventName.contains("TickEvent")) {
                try {
                    String name = (String) event.getClass().getCanonicalName() + "|" + handler.getClass().getSimpleName();
                    dataTick.get(event.getClass(), name).addValue(clock.getDelta());
                } catch (Exception e) {
                    String name = (String) event.getClass().getCanonicalName() + "|" + handler.getClass().getSimpleName();
                    dataTick.put(event.getClass(), name, new DescriptiveStatistics(250));
                    dataModTick.put(event.getClass(), name, ((ModContainer) mod).getName());
                    dataTick.get(event.getClass(), name).addValue(clock.getDelta());
                }

            } else {
                try {
                    String name = (String) event.getClass().getCanonicalName() + "|" + handler.getClass().getSimpleName();
                    data.get(event.getClass(), name).addValue(clock.getDelta());
                } catch (Exception e) {
                    String name = (String) event.getClass().getCanonicalName() + "|" + handler.getClass().getSimpleName();
                    data.put(event.getClass(), name, new DescriptiveStatistics(250));
                    dataMod.put(event.getClass(), name, ((ModContainer) mod).getName());
                    data.get(event.getClass(), name).addValue(clock.getDelta());
                }
            }
        } catch (Exception e) {
            OpisMod.LOGGER.warn(String.format("Error while profiling event %s\n", event));
        }
    }
}
