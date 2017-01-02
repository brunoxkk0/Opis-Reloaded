package mcp.mobius.opis.data.holders.newtypes;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.profilers.ProfilerRenderTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.WeakHashMap;
import net.minecraftforge.common.DimensionManager;

public class DataTileEntityRender extends DataBlockTileEntity {

    public DataTileEntityRender fill(TileEntity ent) {
        this.pos = new CoordinatesBlock(ent.getWorld().provider.getDimension(), ent.getPos().getX(), ent.getPos().getY(), ent.getPos().getZ());
        World world = DimensionManager.getWorld(this.pos.dim);

        this.id = (short) Block.getIdFromBlock(world.getBlockState(new BlockPos(this.pos.x, this.pos.y, this.pos.z)).getBlock());
        this.meta = (short) world.getBlockState(new BlockPos(this.pos.x, this.pos.y, this.pos.z)).getBlock().getMetaFromState(world.getBlockState(new BlockPos(this.pos.x, this.pos.y, this.pos.z)));

        WeakHashMap<TileEntity, DescriptiveStatistics> data = ((ProfilerRenderTileEntity) (ProfilerSection.RENDER_TILEENTITY.getProfiler())).data;
        this.update = new DataTiming(data.containsKey(ent) ? data.get(ent).getGeometricMean() : 0.0D);

        return this;
    }

}
