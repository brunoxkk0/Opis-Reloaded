package mcp.mobius.opis.data.holders.basetypes;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import mcp.mobius.opis.data.holders.ISerializable;

public class AmountHolder implements ISerializable {

    public String key = null;
    public Integer value = 0;

    public AmountHolder(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void writeToStream(ByteArrayDataOutput stream) {
        stream.writeUTF(this.key);
        stream.writeInt(this.value);
    }

    public static AmountHolder readFromStream(ByteArrayDataInput istream) {
        String key = istream.readUTF();
        Integer value = istream.readInt();

        return new AmountHolder(key, value);
    }
}
