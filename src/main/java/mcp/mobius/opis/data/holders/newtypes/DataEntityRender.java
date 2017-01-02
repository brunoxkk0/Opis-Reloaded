package mcp.mobius.opis.data.holders.newtypes;

import java.util.WeakHashMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import mcp.mobius.opis.data.holders.basetypes.CoordinatesBlock;
import mcp.mobius.opis.data.managers.EntityManager;
import mcp.mobius.opis.data.profilers.ProfilerRenderEntity;
import net.minecraft.entity.Entity;

public class DataEntityRender extends DataEntity {

    @Override
    public DataEntityRender fill(Entity entity) {

        this.eid = entity.getEntityId();
        this.name = new CachedString(EntityManager.INSTANCE.getEntityName(entity, false));
        this.pos = new CoordinatesBlock(entity);

        WeakHashMap<Entity, DescriptiveStatistics> data = ((ProfilerRenderEntity) (ProfilerSection.RENDER_ENTITY.getProfiler())).data;
        this.update = new DataTiming(data.containsKey(entity) ? data.get(entity).getGeometricMean() : 0.0D);
        this.npoints = data.containsKey(entity) ? data.get(entity).getN() : 0;

        return this;
    }

}
