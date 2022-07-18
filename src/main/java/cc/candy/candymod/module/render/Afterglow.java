package cc.candy.candymod.module.render;

import cc.candy.candymod.event.events.player.UpdateWalkingPlayerEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.PlayerUtil;
import cc.candy.candymod.utils.RenderUtil;
import cc.candy.candymod.utils.RenderUtil3D;
import cc.candy.candymod.utils.Timer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Afterglow extends Module {
    public Setting<Float> delay = register(new Setting("Delay" , 10.0F , 20.0F , 1.0F));

    public Setting<Color> color = register(new Setting("Color" , new Color(255 , 255 , 255 , 200)));
    public Setting<Float> fadeSpeed = register(new Setting("Fadeout Speed" , 10.0F , 20.0F , 1.0F));
    public Setting<Float> thickness = register(new Setting("Thickness" , 3.0F , 10.0F , 1.0F));

    public static RenderPlayer render = null;
    public List<AfterGlow> glows = new ArrayList<>();
    public Timer timer = new Timer();

    public Afterglow(){
        super("Afterglow" , Categories.RENDER , false , false);
    }

    @SubscribeEvent
    public void onUpdateWalkingEvent(UpdateWalkingPlayerEvent event){
        if(timer == null) timer = new Timer();
        if(timer.passedDms(delay.getValue())) {
            if(mc.player.motionX == 0.0F && mc.player.motionZ == 0.0F) return;
            double[] forward = PlayerUtil.forward(-0.5);
            glows.add(new AfterGlow(forward[0] + mc.player.posX, mc.player.posY, forward[1] + mc.player.posZ, mc.player.rotationYaw, new Color(color.getValue().getRed() , color.getValue().getGreen() , color.getValue().getBlue() , 150)));
        }
    }

    @Override
    public void onRender3D(float ticks){
        if(render == null) render = new RenderPlayer(mc.renderManager);
        //do render shit
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        float posx = (float)(mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * ticks);
        float posy = (float)(mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * ticks);
        float posz = (float)(mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * ticks);
        GlStateManager.translate(posx * -1, posy * -1 , posz * -1);

        for(AfterGlow glow : glows){
            GL11.glPushMatrix();
            GL11.glDepthRange(0.0, 0.01);
            GL11.glDisable(2896);
            GL11.glDisable(3553);
            GL11.glPolygonMode(1032, 6913);
            GL11.glEnable(3008);
            GL11.glEnable(3042);
            GL11.glLineWidth(this.thickness.getValue());
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glColor4f(glow.r / 255.0f, glow.g / 255.0f, glow.b / 255.0f, glow.a / 255.0f);
            mc.getRenderManager().renderEntityStatic(mc.player, 0.0f, false);
            GL11.glHint(3154, 4352);
            GL11.glPolygonMode(1032, 6914);
            GL11.glEnable(2896);
            GL11.glDepthRange(0.0, 1.0);
            GL11.glEnable(3553);
            GL11.glPopMatrix();
            glow.includeAlpha(fadeSpeed.getValue());
        }

        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        //remove dead alpha
        glows.removeIf(g -> g.a >= 255);
    }

    public class AfterGlow{
        public double x, y, z;
        public float yaw;
        public int r,g,b,a;

        public AfterGlow(double x , double y , double z , float yaw , Color color){
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.r = color.getRed();
            this.g = color.getGreen();
            this.b = color.getBlue();
            this.a = color.getAlpha();
        }

        public void includeAlpha(Float speed){
            this.a += speed;
            if(this.a > 255) this.a = 255;
        }
    }
}
