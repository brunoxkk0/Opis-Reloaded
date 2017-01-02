package mcp.mobius.opis.data.holders.newtypes;

import java.util.HashMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import mcp.mobius.opis.data.holders.ISerializable;
import mcp.mobius.opis.data.profilers.ProfilerDimBlockTick;

public class DataBlockTick implements ISerializable {

    public HashMap<Integer, DataTiming> perdim = new HashMap<>();
    public DataTiming total;

    public DataBlockTick fill() {
        this.total = new DataTiming();
        HashMap<Integer, DescriptiveStatistics> data = ((ProfilerDimBlockTick) ProfilerSection.DIMENSION_BLOCKTICK.getProfiler()).data;

        data.keySet().stream().map((dim) -> {
            this.perdim.put(dim, new DataTiming(data.get(dim).getGeometricMean()));
            return dim;
        }).forEachOrdered((dim) -> {
            this.total.timing += data.get(dim).getGeometricMean();
        });

        return this;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeShort(this.perdim.size());
        this.perdim.keySet().stream().map((key) -> {
            stream.writeInt(key);
            return key;
        }).forEachOrdered((key) -> {
            this.perdim.get(key).writeToStream(stream);
        });
        this.total.writeToStream(stream);
    }

    public static DataBlockTick readFromStream(ByteArrayDataInput stream) {
        DataBlockTick retVal = new DataBlockTick();
        int nkeys = stream.readShort();
        for (int i = 0; i < nkeys; i++) {
            retVal.perdim.put(stream.readInt(), DataTiming.readFromStream(stream));
        }
        retVal.total = DataTiming.readFromStream(stream);
        return retVal;
    }
}
