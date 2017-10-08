package mcp.mobius.opis.mixin.client;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderManager.class, priority = 1001)
public abstract class MixinRenderManager {

    @Inject(method = "renderEntity", at = @At("HEAD"))
    public void renderEntityStart(Entity entity, double x, double y, double z, float yaw, float partialTicks, boolean p_147939_10_, CallbackInfo ci) {
        ProfilerSection.RENDER_ENTITY.start(entity);
    }

    @Inject(method = "renderEntity", at = @At("HEAD"))
    public void renderEntityStop(Entity entity, double x, double y, double z, float yaw, float partialTicks, boolean p_147939_10_, CallbackInfo ci) {
        ProfilerSection.RENDER_ENTITY.start(entity);
    }

}
