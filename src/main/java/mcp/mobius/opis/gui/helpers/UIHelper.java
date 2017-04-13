package mcp.mobius.opis.gui.helpers;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class UIHelper {

    public static void drawTexture(int posX, int posY, int sizeX, int sizeY) {
        UIHelper.drawTexture(posX, posY, sizeX, sizeY, 0, 0, 256, 256);
    }

    public static void drawTexture(int posX, int posY, int sizeX, int sizeY, int texU, int texV, int texSizeU, int texSizeV) {
        float zLevel = 0.0F;
        float f = 0.00390625F;

        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buf = tess.getBuffer();
        buf.begin(7, DefaultVertexFormats.POSITION_TEX);
        buf.pos((double) (posX + 0), (double) (posY + sizeY), (double) zLevel).tex(texU * f, (texV + texSizeV) * f).endVertex();
        buf.pos((double) (posX + sizeX), (double) (posY + sizeY), (double) zLevel).tex((texU + texSizeU) * f, (texV + texSizeV) * f).endVertex();
        buf.pos((double) (posX + sizeX), (double) (posY + 0), (double) zLevel).tex((texU + texSizeU) * f, texV * f).endVertex();
        buf.pos((double) (posX + 0), (double) (posY + 0), (double) zLevel).tex(texU * f, texV * f).endVertex();
        tess.draw();
    }

    public static void drawGradientRect(int minx, int miny, int maxx, int maxy, int zlevel, int color1, int color2) {
        float alpha1 = (float) (color1 >> 24 & 255) / 255.0F;
        float red1 = (float) (color1 >> 16 & 255) / 255.0F;
        float green1 = (float) (color1 >> 8 & 255) / 255.0F;
        float blue1 = (float) (color1 & 255) / 255.0F;
        float alpha2 = (float) (color2 >> 24 & 255) / 255.0F;
        float red2 = (float) (color2 >> 16 & 255) / 255.0F;
        float green2 = (float) (color2 >> 8 & 255) / 255.0F;
        float blue2 = (float) (color2 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buf = tess.getBuffer();
        buf.begin(7, DefaultVertexFormats.POSITION_TEX);
        //		tessellator.setColorRGBA_F(red1, green1, blue1, alpha1);
        buf.pos((double) maxx, (double) miny, (double) zlevel).endVertex();
        buf.pos((double) minx, (double) miny, (double) zlevel).endVertex();
        //		tess.setColorRGBA_F(red2, green2, blue2, alpha2);
        buf.pos((double) minx, (double) maxy, (double) zlevel).endVertex();
        buf.pos((double) maxx, (double) maxy, (double) zlevel).endVertex();
        tess.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void drawLine(int x, int y, int x2, int y2, int width, float... rgba) {

        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buf = tess.getBuffer();
        GL11.glPushMatrix();
        //GL11.glLineWidth(3);
        GL11.glLineWidth(width);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        buf.begin(3, DefaultVertexFormats.POSITION);

        if (rgba.length == 4) //			tess.setColorRGBA_F(rgba[0], rgba[1], rgba[2], rgba[3]);
        //tess.setColorRGBA_F(1,1,1,1);
        {
            buf.pos(x, y, 0).endVertex();
        }
        buf.pos(x2, y2, 0).endVertex();

        tess.draw();
        GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();

    }

}
