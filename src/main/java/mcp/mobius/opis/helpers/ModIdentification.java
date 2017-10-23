package mcp.mobius.opis.helpers;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ModIdentification {

    public static String getStackName(int id, int meta) {
        ItemStack is;
        String name = String.format("te.%d.%d", id, meta);

        try {
            is = new ItemStack(Block.getBlockById(id), 1, meta);

            name = is.getDisplayName();
        } catch (Exception e) {
        }

        return name;
    }

    public static String getModStackName(int id, int meta) {
        ItemStack is;
        String modID = "<Unknown>";

        try {
            is = new ItemStack(Block.getBlockById(id), 1, meta);

            modID = is.getItem().getRegistryName().getResourceDomain();
        } catch (Exception e) {
        }

        return modID;
    }
}
