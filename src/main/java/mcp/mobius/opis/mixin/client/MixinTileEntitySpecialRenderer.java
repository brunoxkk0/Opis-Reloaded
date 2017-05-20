package mcp.mobius.opis.mixin.client;

import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntitySpecialRenderer.class, priority = 1001)
public abstract class MixinTileEntitySpecialRenderer extends Object {

    @Inject(method = "renderTileEntityAt", at = @At("HEAD"))
    public void renderTileEntityAtStart(TileEntity tileentity, double d, double d1, double d2, float f, int i, CallbackInfo ci) {
        ProfilerSection.RENDER_TILEENTITY.start(tileentity);
    }

    @Inject(method = "renderTileEntityAt", at = @At("RETURN"))
    public void renderTileEntityAtEnd(TileEntity tileentity, double d, double d1, double d2, float f, int i, CallbackInfo ci) {
        ProfilerSection.RENDER_TILEENTITY.stop(tileentity);
    }
}
