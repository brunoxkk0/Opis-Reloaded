package mcp.mobius.opis.mixin.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import java.util.List;
import mcp.mobius.opis.data.profilers.ProfilerSection;
import net.minecraft.network.NettyPacketDecoder;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = NettyPacketDecoder.class, priority = 2000)
public abstract class MixinNettyPacketDecoder {

    @Inject(method = "decode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;readPacketData(Lnet/minecraft/network/PacketBuffer;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    protected void onDecode(ChannelHandlerContext context, ByteBuf byteBuf, List<Object> objList, CallbackInfo ci, PacketBuffer buffer, int value, Packet packet) throws IOException, InstantiationException, IllegalAccessException, Exception {
        ProfilerSection.PACKET_INBOUND.start(packet);
    }
}
