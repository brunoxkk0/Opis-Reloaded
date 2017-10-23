package mcp.mobius.opis.helpers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagShort;

public class NBTUtil {

    public static NBTBase getTag(String key, NBTTagCompound tag) {
        String[] path = key.split("\\.");

        NBTTagCompound deepTag = tag;
        for (String i : path) {
            if (deepTag.hasKey(i)) {
                if (deepTag.getTag(i) instanceof NBTTagCompound) {
                    deepTag = deepTag.getCompoundTag(i);
                } else {
                    return deepTag.getTag(i);
                }
            } else {
                return null;
            }
        }
        return deepTag;
    }

    public static void writeNBTTagCompound(NBTTagCompound par0NBTTagCompound, DataOutputStream ostream) throws IOException {
        if (par0NBTTagCompound == null) {
            ostream.writeShort(-1);
        } else {
            CompressedStreamTools.write(par0NBTTagCompound, ostream);
        }
    }

    public static NBTTagCompound readNBTTagCompound(DataInputStream istream) throws IOException {
        short short1 = istream.readShort();

        if (short1 < 0) {
            return null;
        } else {
            return CompressedStreamTools.read(istream);
        }
    }

    public static int getNBTInteger(NBTTagCompound tag, String keyname) {
        NBTBase subtag = tag.getTag(keyname);
        if (subtag instanceof NBTTagInt) {
            return tag.getInteger(keyname);
        }
        if (subtag instanceof NBTTagShort) {
            return tag.getShort(keyname);
        }
        if (subtag instanceof NBTTagByte) {
            return tag.getByte(keyname);
        }

        return 0;
    }

}
