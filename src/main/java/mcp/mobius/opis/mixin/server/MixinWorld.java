package mcp.mobius.opis.mixin.server;

import java.util.Iterator;
import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = World.class, priority = 1001)
public abstract class MixinWorld {

    @Inject(method = "updateEntityWithOptionalForce", at = @At("HEAD"))
    public void updateEntityStart(Entity entity, boolean force, CallbackInfo ci) {
        ProfilerSection.ENTITY_UPDATETIME.start(entity);
    }

    @Inject(method = "updateEntityWithOptionalForce", at = @At("RETURN"))
    public void updateEntityStop(Entity entity, boolean force, CallbackInfo ci) {
        ProfilerSection.ENTITY_UPDATETIME.stop(entity);
    }

    @Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ITickable;update()V", shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    protected void updateTileEntityStart(CallbackInfo ci, Iterator iterator, TileEntity tileEntity, BlockPos blockPos) {
        ProfilerSection.TILEENT_UPDATETIME.start(tileEntity);
    }

    @Inject(method = "updateEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ITickable;update()V", shift = Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    protected void updateTileEntityEnd(CallbackInfo ci, Iterator iterator, TileEntity tileEntity, BlockPos blockPos, ITickable tickable) {
        ProfilerSection.TILEENT_UPDATETIME.stop(tileEntity);
    }
}
