package cc.candy.candymod.module.render;

import cc.candy.candymod.event.events.player.UpdateWalkingPlayerEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.PlayerUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BreadCrumbs extends Module {
    public Setting<Color> color = register(new Setting("Color" , new Color(130 , 10 , 220 , 200)));
    public Setting<Float> fadeSpeed = register(new Setting("Fadeout Speed" , 10.0F , 20.0F , 1.0F));
    public Setting<Float> thickness = register(new Setting("Thickness" , 3.0F , 10.0F , 1.0F));
    public Setting<Float> offset = register(new Setting("OffsetY" , 5.0F , 10.0F , 0.0F));
    public Setting<Boolean> other = register(new Setting("Other" , false));

    public List<Trace> traces = new ArrayList<>();
    public BreadCrumbs(){
        super("BreadCrumbs" , Categories.RENDER , false , false);
    }

    @Override
    public void onRender3D(float ticks){
        try{
            doRender(ticks);
        }
        catch (Exception ignored){

        }
    }

    @SubscribeEvent
    public void onUpdateWalkingEvent(UpdateWalkingPlayerEvent event){
        traces.add(new Trace(mc.player.posX , mc.player.posY + (offset.getValue() - 5.0) , mc.player.posZ , color.getValue()));
    }

    public void doRender(float ticks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        float posx = (float)(mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * ticks);
        float posy = (float)(mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * ticks);
        float posz = (float)(mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * ticks);
        GlStateManager.translate(posx * -1 , posy * -1 , posz * -1);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();

        float thickness = this.thickness.getValue();
        GL11.glEnable(2848);
        GL11.glLineWidth(thickness);
        worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for(Trace trace : traces){
            int r = trace.r;
            int g = trace.g;
            int b = trace.b;
            int a = trace.a;
            worldRenderer.pos(trace.x , trace.y , trace.z).color(r , g , b , a).endVertex();
            trace.includeAlpha(fadeSpeed.getValue());
        }
        tessellator.draw();

        GL11.glLineWidth(1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
        //remove dead alpha
        traces.removeIf(trace -> trace.a <= 0);
    }

    public class Trace{
        public double x, y , z;
        public int r,g,b,a;

        public Trace(double x , double y , double z , Color color){
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = color.getRed();
            this.g = color.getGreen();
            this.b = color.getBlue();
            this.a = color.getAlpha();
        }

        public void includeAlpha(Float speed){
            this.a -= speed;
            if(this.a < 0) this.a = 0;
        }
    }
}
