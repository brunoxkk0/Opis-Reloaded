package mcp.mobius.opis.data.monitors;

import com.google.common.collect.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;

import java.util.*;
import mcp.mobius.opis.ModOpis;

public class MonitoredTileList<E> extends MonitoredList<E> {

    //private Map<String, Integer> count = new HashMap<String, Integer>();
    private final Table<IBlockState, Integer, Integer> count = HashBasedTable.create();
    private final Map<Object, BlockData> blockdata = new WeakHashMap();

    private class BlockData {

        public final IBlockState block;
        public final int meta;

        public BlockData(IBlockState b, int m) {
            this.block = b;
            this.meta = m;
        }
    }

    @Override
    protected void addCount(E e) {
        TileEntity te = ((TileEntity) e);
        if (te == null || te.getWorld() == null) {
            return;
        }

        IBlockState block = te.getWorld().getBlockState(te.getPos());
        int meta = block.getBlock().getMetaFromState(block);

        blockdata.put(te, new BlockData(block, meta));

        try {
            count.put(block, meta, count.get(block, meta) + 1);
        } catch (NullPointerException ex) {
            count.put(block, meta, 1);
        } catch (Exception ex) {
            ex.printStackTrace();
            count.put(block, meta, 1);
        }
    }

    @Override
    protected void removeCount(int index) {
        this.removeCount(this.get(index));
    }

    @Override
    protected void removeCount(Object o) {
        if (blockdata.containsKey(o)) {
            BlockData d = blockdata.get(o);
            try {
                this.count.put(d.block, d.meta, this.count.get(d.block, d.meta) - 1);
            } catch (NullPointerException e) {
                this.count.put(d.block, d.meta, 0);
            }
        }
    }

    @Override
    protected void clearCount() {
        this.count.clear();
    }

    @Override
    public void printCount() {
        this.count.cellSet().forEach((c) -> {
            ModOpis.log.info(String.format("%s | %s : %s", c.getRowKey(), c.getColumnKey(), c.getValue()));
        });

    }

    public Table<IBlockState, Integer, Integer> getCount() {
        return this.count;
    }
}
