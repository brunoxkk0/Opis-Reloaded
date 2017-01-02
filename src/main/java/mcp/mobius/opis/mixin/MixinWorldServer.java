package mcp.mobius.opis.mixin;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldServer.class, priority = 1001)
public class MixinWorldServer {
    
    @Inject(method = "tick", at = @At("HEAD"))
    public void tickStart(CallbackInfo ci) {
        ProfilerSection.TICK.start();
    }
    
    @Inject(method = "tick", at = @At("RETURN"))
    public void tickStop(CallbackInfo ci) {
        ProfilerSection.TICK.stop();
    }
    
}
