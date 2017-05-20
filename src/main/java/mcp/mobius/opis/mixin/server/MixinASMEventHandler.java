package mcp.mobius.opis.mixin.server;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ASMEventHandler.class, priority = 1001, remap = false)
public abstract class MixinASMEventHandler implements IEventListener {

    @Shadow
    @Final
    private IEventListener handler;

    @Shadow
    private ModContainer owner;

    @Inject(method = "invoke", at = @At("HEAD"))
    public void onInvokeStart(Event event, CallbackInfo ci) {
        if (owner != null && handler != null) {
            ProfilerSection.EVENT_INVOKE.start();
        }
    }

    @Inject(method = "invoke", at = @At("RETURN"))
    public void onInvokeStop(Event event, CallbackInfo ci) {
        if (owner != null && handler != null) {
            ProfilerSection.EVENT_INVOKE.stop(event, handler, owner);
        }
    }
}
