package mcp.mobius.opis.mixin.server;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldServer.class, priority = 1001)
public abstract class MixinWorldServer extends World {

    public MixinWorldServer(ISaveHandler saveHandler, WorldInfo info, WorldProvider provider, Profiler profiler, boolean bool) {
        super(saveHandler, info, provider, profiler, bool);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickStart(CallbackInfo ci) {
        ProfilerSection.DIMENSION_BLOCKTICK.start(provider.getDimension());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickStop(CallbackInfo ci) {
        ProfilerSection.DIMENSION_BLOCKTICK.stop(provider.getDimension());
    }
}
