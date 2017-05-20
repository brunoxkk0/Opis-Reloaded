package mcp.mobius.opis.mixin.server;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FMLCommonHandler.class, priority = 1001, remap = false)
public abstract class MixinFMLCommonHandler {

    @Inject(method = "onPreServerTick", at = @At("HEAD"))
    public void onPreServerTick(CallbackInfo ci) {
        ProfilerSection.TICK.start();
    }

    @Inject(method = "onPostServerTick", at = @At("RETURN"))
    public void onPostServerTick(CallbackInfo ci) {
        ProfilerSection.TICK.stop();
    }

    @Inject(method = "onPreWorldTick", at = @At("HEAD"))
    public void onPreWorldTick(CallbackInfo ci) {
        ProfilerSection.DIMENSION_TICK.start();
    }

    @Inject(method = "onPostWorldTick", at = @At("RETURN"))
    public void onPostWorldTick(CallbackInfo ci) {
        ProfilerSection.DIMENSION_TICK.stop();
    }
}
