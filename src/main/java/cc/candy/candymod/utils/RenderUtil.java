package cc.candy.candymod.utils;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.gui.font.CFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import scala.Char;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil implements Util {

    private static ICamera camera = new Frustum();

    public static void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y + h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x + w, y + h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x + w, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawGradientRect(float left, float top, float right, float bottom, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) left, (double) top, (double) 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) left, (double) bottom, (double) 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double) right, (double) bottom, (double) 0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double) right, (double) top, (double) 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawLine(float x, float y, float x1, float y1, float thickness, int hex) {
        float red = (float) (hex >> 16 & 0xFF) / 255.0f;
        float green = (float) (hex >> 8 & 0xFF) / 255.0f;
        float blue = (float) (hex & 0xFF) / 255.0f;
        float alpha = (float) (hex >> 24 & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GL11.glLineWidth(thickness);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x1, y1, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)(xCoord + 0.0F), (double)(yCoord + (float)maxV), (double)0).tex((double)((float)(minU + 0) * 0.00390625F), (double)((float)(minV + maxV) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(xCoord + (float)maxU), (double)(yCoord + (float)maxV), (double)0).tex((double)((float)(minU + maxU) * 0.00390625F), (double)((float)(minV + maxV) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(xCoord + (float)maxU), (double)(yCoord + 0.0F), (double)0).tex((double)((float)(minU + maxU) * 0.00390625F), (double)((float)(minV + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(xCoord + 0.0F), (double)(yCoord + 0.0F), (double)0).tex((double)((float)(minU + 0) * 0.00390625F), (double)((float)(minV + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
    }

    //from Salhack
    public static void drawTexture(float x, float y, float textureX, float textureY, float width, float height) {
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopAttrib();

        float f = 0.00390625F;
        float f1 = 0.00390625F;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.color(0, 0, 0, 150);
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, (y + height), 0.0D).tex((textureX * f), ((textureY + height) * f1)).endVertex();
        bufferbuilder.pos((x + width), (y + height), 0.0D).tex(((textureX + width) * f), ((textureY + height) * f1)).endVertex();
        bufferbuilder.pos((x + width), y, 0.0D).tex(((textureX + width) * f), (textureY * f1)).endVertex();
        bufferbuilder.pos(x, y, 0.0D).tex((textureX * f), (textureY * f1)).endVertex();
        tessellator.draw();
    }

    public static void drawTexture(float x, float y, float width, float height, float u, float v, float t, float s)
    {
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopAttrib();

        GlStateManager.depthMask(false);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x + width, y, 0F).tex(t, v).endVertex();
        bufferbuilder.pos(x, y, 0F).tex(u, v).endVertex();
        bufferbuilder.pos(x, y + height, 0F).tex(u, s).endVertex();
        bufferbuilder.pos(x, y + height, 0F).tex(u, s).endVertex();
        bufferbuilder.pos(x + width, y + height, 0F).tex(t, s).endVertex();
        bufferbuilder.pos(x + width, y, 0F).tex(t, v).endVertex();
        tessellator.draw();
    }

    public static void drawTexture(float posX, float posY, float width, float height) {
        GL11.glPushMatrix();

        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0.0f, 0.0f);
        GL11.glEnd();

        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GL11.glPopMatrix();
    }

    //return value : string width
    public static float drawString(String str, float x, float y, int color, boolean shadow, float s) {
        CFontRenderer font = CandyMod.m_font.fontRenderer;
        if (shadow)
            font.drawString(str, x + (0.5 * s), y + (0.5 * s), ColorUtil.toRGBA(new Color(0, 0, 0, 255)), true, 1.0F);
        return font.drawString(str, x, y, color, false, s);
    }

    public static float getStringWidth(String str, float s) {
        CFontRenderer font = CandyMod.m_font.fontRenderer;
        return font.getStringWidth(str) * s;
    }

    public static float getStringHeight(float s) {
        CFontRenderer font = CandyMod.m_font.fontRenderer;
        return (font.getHeight() - 1) * s;
    }

    public static int getRainbow(int speed, int offset, float s, float b) {
        float hue = (System.currentTimeMillis() + (long) offset) % (long) speed;
        return Color.getHSBColor(hue /= (float) speed, s, b).getRGB();
    }

    public static void renderItem(ItemStack item, float x , float y) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().zLevel = -150.0f;
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item, (int)x, (int)y);
        Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer, item, (int)x, (int)y);
        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0F;
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
    }

    public static void renderItem(ItemStack item, float x , float y , boolean itemOverlay) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().zLevel = -150.0f;
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item, (int)x, (int)y);
        if(itemOverlay) Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer, item, (int)x, (int)y);
        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0F;
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
    }

    public static void renderEntity(EntityLivingBase entity, float x , float y, float scale) {
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);
        RenderHelper.enableGUIStandardItemLighting();
        drawEntityOnScreen((int)x, (int)y, (int)scale, entity);
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.depthMask(false);
    }

    private static void drawEntityOnScreen(int posX, int posY, int scale, EntityLivingBase ent){
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.getEntityRenderObject(ent).doRender(ent , 0.0 , 0.0 , 0.0 , ent.rotationYaw , 1.0F);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static float guiToScreen(float shit){
        final ScaledResolution scaledresolution = new ScaledResolution(mc);
        final double scaleFactor = scaledresolution.getScaleFactor();
        return (float) (shit * scaleFactor);
    }

    private static Rectangle _lasted;
    private static Rectangle lasted;

    public static void Scissor(float x , float y , float width , float height){
        _lasted = new Rectangle((int)x , (int)y , (int)width , (int)height);
        GL11.glScissor((int)guiToScreen(x) , (int) ((int)mc.displayHeight - (guiToScreen(y) + guiToScreen(height))), (int)guiToScreen(width) , (int)guiToScreen(height));
    }

    public static void pushScissor(){
        lasted = _lasted;
    }

    public static void popScissor(){
        GL11.glScissor((int)guiToScreen(lasted.x) , (int) ((int)mc.displayHeight - (guiToScreen(lasted.y) + guiToScreen(lasted.height))), (int)guiToScreen(lasted.width) , (int)guiToScreen(lasted.height));
    }
}