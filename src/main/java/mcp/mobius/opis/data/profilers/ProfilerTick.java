package mcp.mobius.opis.data.profilers;

import mcp.mobius.opis.OpisMod;
import mcp.mobius.opis.data.profilers.Clock.IClock;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class ProfilerTick extends ProfilerAbstract {

    private final IClock clock = Clock.getNewClock();
    public DescriptiveStatistics data = new DescriptiveStatistics(20);

    @Override
    public void reset() {
        //this.data.clear();
    }

    @Override
    public void start() {
        //DeadManSwitch.instance.setTimer(System.nanoTime());
        this.clock.start();
    }

    @Override
    public void stop() {
        this.clock.stop();
        try {
            this.data.addValue(clock.getDelta());
        } catch (Exception e) {
            OpisMod.LOGGER.warn("Error while profiling tick.");
        }
    }

}
