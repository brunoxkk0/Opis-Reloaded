package mcp.mobius.opis.mixin.common;

import io.netty.channel.ChannelHandlerContext;
import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetworkManager.class, priority = 2000)
public class MixinNetworkManager {
    
    @Inject(method = "channelRead0", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;processPacket(Lnet/minecraft/network/INetHandler;)V"))
    protected void onChannelRead0(ChannelHandlerContext context, Packet packet, CallbackInfo ci)  {
        ProfilerSection.PACKET_INBOUND.stop(packet);
    }
    
}
