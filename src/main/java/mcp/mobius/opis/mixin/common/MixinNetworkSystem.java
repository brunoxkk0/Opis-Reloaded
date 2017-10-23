package mcp.mobius.opis.mixin.common;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraft.network.NetworkSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetworkSystem.class, priority = 1001)
public class MixinNetworkSystem {
    
    @Inject(method = "networkTick", at = @At("HEAD"))
    public void onNetworkTickStart(CallbackInfo ci)  {
        ProfilerSection.NETWORK_TICK.start();
    }
    
    @Inject(method = "networkTick", at = @At("RETURN"))
    public void onNetworkTickStop(CallbackInfo ci)  {
        ProfilerSection.NETWORK_TICK.stop();
    }
    
}
