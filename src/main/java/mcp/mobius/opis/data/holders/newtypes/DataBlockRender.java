package mcp.mobius.opis.data.holders.newtypes;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.profilers.ProfilerRenderBlock;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.HashMap;
import net.minecraftforge.common.DimensionManager;

public class DataBlockRender extends DataBlockTileEntity {

    @Override
    public DataBlockRender fill(CoordinatesBlock coord) {
        this.pos = coord;
        World world = DimensionManager.getWorld(this.pos.dim);

        this.id = (short) Block.getIdFromBlock(world.getBlockState(new BlockPos(this.pos.x, this.pos.y, this.pos.z)).getBlock());
        this.meta = (short) world.getBlockState(new BlockPos(this.pos.x, this.pos.y, this.pos.z)).getBlock().getMetaFromState(world.getBlockState(new BlockPos(this.pos.x, this.pos.y, this.pos.z)));

        HashMap<CoordinatesBlock, DescriptiveStatistics> data = ((ProfilerRenderBlock) (ProfilerSection.RENDER_BLOCK.getProfiler())).data;
        this.update = new DataTiming(data.containsKey(coord) ? data.get(coord).getGeometricMean() : 0.0D);

        return this;
    }

}
