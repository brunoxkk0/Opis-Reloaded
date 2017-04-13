package mcp.mobius.opis.events;

import java.util.Optional;
import mcp.mobius.opis.OpisMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.*;
import org.lwjgl.opengl.GL11;

public class OpisClientEventHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (OpisMod.selectedBlock == null) {
            return;
        }
        if (Minecraft.getMinecraft().world.provider.getDimension() != OpisMod.selectedBlock.dim) {
            return;
        }
        if (Minecraft.getMinecraft().world.isAirBlock(new BlockPos(OpisMod.selectedBlock.x, OpisMod.selectedBlock.y, OpisMod.selectedBlock.z))) {
            return;
        }

        double partialTicks = event.getPartialTicks();

        Optional<Entity> player = Optional.ofNullable(Minecraft.getMinecraft().getRenderViewEntity());
        if (player.isPresent()) {
            double px = player.get().lastTickPosX + (player.get().posX - player.get().lastTickPosX) * partialTicks;
            double py = player.get().lastTickPosY + (player.get().posY - player.get().lastTickPosY) * partialTicks;
            double pz = player.get().lastTickPosZ + (player.get().posZ - player.get().lastTickPosZ) * partialTicks;

            int bx = OpisMod.selectedBlock.x;
            int by = OpisMod.selectedBlock.y;
            int bz = OpisMod.selectedBlock.z;

            double offset = 0.02;
            double delta = 1 + 2 * offset;

            double x = bx - px - offset;
            double y = by - py - offset;
            double z = bz - pz - offset;

            GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthMask(false);

            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buf = tessellator.getBuffer();
            buf.begin(7, DefaultVertexFormats.POSITION);
            //            tessellator.setColorRGBA(255, 0, 0, 150);

            buf.pos(x, y, z).endVertex();
            buf.pos(x + delta, y, z).endVertex();
            buf.pos(x + delta, y, z + delta).endVertex();
            buf.pos(x, y, z + delta).endVertex();

            buf.pos(x, y + delta, z).endVertex();
            buf.pos(x, y + delta, z + delta).endVertex();
            buf.pos(x + delta, y + delta, z + delta).endVertex();
            buf.pos(x + delta, y + delta, z).endVertex();

            buf.pos(x, y, z).endVertex();
            buf.pos(x, y + delta, z).endVertex();
            buf.pos(x + delta, y + delta, z).endVertex();
            buf.pos(x + delta, y, z).endVertex();

            buf.pos(x, y, z + delta).endVertex();
            buf.pos(x + delta, y, z + delta).endVertex();
            buf.pos(x + delta, y + delta, z + delta).endVertex();
            buf.pos(x, y + delta, z + delta).endVertex();

            buf.pos(x, y, z).endVertex();
            buf.pos(x, y, z + delta).endVertex();
            buf.pos(x, y + delta, z + delta).endVertex();
            buf.pos(x, y + delta, z).endVertex();

            buf.pos(x + delta, y, z).endVertex();
            buf.pos(x + delta, y + delta, z).endVertex();
            buf.pos(x + delta, y + delta, z + delta).endVertex();
            buf.pos(x + delta, y, z + delta).endVertex();

            tessellator.draw();

            GL11.glEnable(GL11.GL_TEXTURE_2D);

            GL11.glPopAttrib();
        }
    }
}
