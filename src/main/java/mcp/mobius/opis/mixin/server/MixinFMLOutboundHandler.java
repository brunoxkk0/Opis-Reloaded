package mcp.mobius.opis.mixin.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = FMLOutboundHandler.class, priority = 1001, remap = false)
public abstract class MixinFMLOutboundHandler {
    
    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/handshake/NetworkDispatcher;sendProxy(Lnet/minecraftforge/fml/common/network/internal/FMLProxyPacket;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onSendProxy(ChannelHandlerContext context, Object obj, ChannelPromise promise, CallbackInfo ci, FMLProxyPacket packet) {
        ProfilerSection.PACKET_OUTBOUND.stop(packet);
    }
}
