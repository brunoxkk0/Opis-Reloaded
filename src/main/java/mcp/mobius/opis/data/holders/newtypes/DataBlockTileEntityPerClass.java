package mcp.mobius.opis.data.holders.newtypes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import mcp.mobius.opis.data.holders.ISerializable;

public class DataBlockTileEntityPerClass implements ISerializable, Comparable {

    public int id;
    public int meta;
    public int amount;
    public DataTiming update;

    public DataBlockTileEntityPerClass() {
    }

    public DataBlockTileEntityPerClass(int id, int meta) {
        this.id = id;
        this.meta = meta;
        this.amount = 0;
        this.update = new DataTiming();
    }

    public DataBlockTileEntityPerClass add() {
        this.amount += 1;
        return this;
    }

    public DataBlockTileEntityPerClass add(Double timing) {
        this.amount += 1;
        this.update.timing += timing;

        return this;
    }

    public DataBlockTileEntityPerClass add(int amount, Double timing) {
        this.amount += amount;
        this.update.timing += timing;

        return this;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeInt(this.id);
        stream.writeInt(this.meta);
        stream.writeInt(this.amount);
        this.update.writeToStream(stream);
    }

    public static DataBlockTileEntityPerClass readFromStream(ByteArrayDataInput stream) {
        DataBlockTileEntityPerClass retVal = new DataBlockTileEntityPerClass();
        retVal.id = stream.readInt();
        retVal.meta = stream.readInt();
        retVal.amount = stream.readInt();
        retVal.update = DataTiming.readFromStream(stream);
        return retVal;
    }

    @Override
    public int compareTo(Object o) {
        return this.update.compareTo(((DataBlockTileEntityPerClass) o).update);
    }

}
