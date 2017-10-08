package mcp.mobius.opis.mixin.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NettyPacketEncoder;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = NettyPacketEncoder.class, priority = 2000)
public abstract class MixinNettyPacketEncoder {

    @Inject(method = "encode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;writePacketData(Lnet/minecraft/network/PacketBuffer;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    protected void onEncode(ChannelHandlerContext context, Packet packet, ByteBuf byteBuf, CallbackInfo ci, EnumConnectionState enumconnectionstate, Integer value, PacketBuffer buffer) throws IOException, Exception {
        ProfilerSection.PACKET_OUTBOUND.start(packet, buffer.capacity());
    }
}
