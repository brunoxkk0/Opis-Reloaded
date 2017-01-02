package mcp.mobius.opis.mixin;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = World.class, priority = 1001)
public class MixinWorld {
    
    @Inject(method = "updateEntities", at = @At("HEAD"))
    public void updateEntitiesStart(CallbackInfo ci) {
        ProfilerSection.TILEENT_UPDATETIME.start();
    }
    
    @Inject(method = "updateEntities", at = @At("RETURN"))
    public void updateEntitiesStop(CallbackInfo ci) {
        ProfilerSection.TILEENT_UPDATETIME.stop();
    }
    
    @Inject(method = "updateEntity", at = @At("HEAD"))
    public void updateEntityStart(Entity entity, CallbackInfo ci) {
        ProfilerSection.ENTITY_UPDATETIME.start();
    }
    
    @Inject(method = "updateEntity", at = @At("RETURN"))
    public void updateEntityStop(Entity entity, CallbackInfo ci) {
        ProfilerSection.ENTITY_UPDATETIME.stop();
    }
    
    
}
