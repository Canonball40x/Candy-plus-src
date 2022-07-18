package cc.candy.candymod.mixin.mixins;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.event.events.render.RenderEntityModelEvent;
import cc.candy.candymod.module.render.CandyCrystal;
import cc.candy.candymod.utils.EntityUtil;
import cc.candy.candymod.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(value={RenderEnderCrystal.class})
public class MixinRenderEnderCrystal {
    @Redirect(method={"doRender"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBaseHook(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        CandyCrystal candycrystal = (CandyCrystal) CandyMod.m_module.getModuleWithClass(CandyCrystal.class);
        if(candycrystal == null) return;

        GlStateManager.scale((float) candycrystal.scale.getValue(), (float)candycrystal.scale.getValue().floatValue(), (float)candycrystal.scale.getValue().floatValue());

        if (candycrystal.isEnable && candycrystal.wireframe.getValue()) {
            RenderEntityModelEvent event = new RenderEntityModelEvent(model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            MinecraftForge.EVENT_BUS.post(event);
        }

        if (candycrystal.isEnable && candycrystal.chams.getValue()) {
            GL11.glPushAttrib((int)1048575);
            GL11.glDisable((int)3008);
            GL11.glDisable((int)3553);
            GL11.glDisable((int)2896);
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glLineWidth((float)1.5f);
            GL11.glEnable((int)2960);
            if (candycrystal.rainbow.getValue().booleanValue()) {
                Color rainbowColor1 = candycrystal.colorSync.getValue() != false ? candycrystal.getCurrentColor() : new Color(RenderUtil.getRainbow(candycrystal.speed.getValue() * 100, 0, (float)candycrystal.saturation.getValue().intValue() / 100.0f, (float)candycrystal.brightness.getValue().intValue() / 100.0f));
                Color rainbowColor = new Color(rainbowColor1.getRed(), rainbowColor1.getGreen(), rainbowColor1.getBlue());
                if (candycrystal.throughWalls.getValue().booleanValue()) {
                    GL11.glDisable((int)2929);
                    GL11.glDepthMask((boolean)false);
                }
                GL11.glEnable((int)10754);
                GL11.glColor4f((float)((float)rainbowColor.getRed() / 255.0f), (float)((float)rainbowColor.getGreen() / 255.0f), (float)((float)rainbowColor.getBlue() / 255.0f), (float)((float) candycrystal.alpha.getValue() / 255.0f));
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (candycrystal.throughWalls.getValue()) {
                    GL11.glEnable((int)2929);
                    GL11.glDepthMask((boolean)true);
                }
            } else if (candycrystal.xqz.getValue().booleanValue() && candycrystal.throughWalls.getValue().booleanValue()) {
                Color hiddenColor = candycrystal.hiddenColor.getValue();
                Color color = candycrystal.color.getValue();
                Color visibleColor = new Color(color.getRed() , color.getGreen() , color.getBlue() , color.getAlpha());
                if (candycrystal.throughWalls.getValue().booleanValue()) {
                    GL11.glDisable((int)2929);
                    GL11.glDepthMask((boolean)false);
                }
                GL11.glEnable((int)10754);
                GL11.glColor4f((float)((float)hiddenColor.getRed() / 255.0f), (float)((float)hiddenColor.getGreen() / 255.0f), (float)((float)hiddenColor.getBlue() / 255.0f), (float)((float)candycrystal.alpha.getValue().intValue() / 255.0f));
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (candycrystal.throughWalls.getValue().booleanValue()) {
                    GL11.glEnable((int)2929);
                    GL11.glDepthMask((boolean)true);
                }
                GL11.glColor4f((float)((float)visibleColor.getRed() / 255.0f), (float)((float)visibleColor.getGreen() / 255.0f), (float)((float)visibleColor.getBlue() / 255.0f), (float)((float)candycrystal.alpha.getValue().intValue() / 255.0f));
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            } else {
                Color visibleColor;
                Color color = visibleColor = candycrystal.colorSync.getValue() ? candycrystal.getCurrentColor() : candycrystal.color.getValue();
                if (candycrystal.throughWalls.getValue()) {
                    GL11.glDisable((int)2929);
                    GL11.glDepthMask((boolean)false);
                }
                GL11.glEnable((int)10754);
                GL11.glColor4f((float)((float)visibleColor.getRed() / 255.0f), (float)((float)visibleColor.getGreen() / 255.0f), (float)((float)visibleColor.getBlue() / 255.0f), (float)((float)candycrystal.alpha.getValue().intValue() / 255.0f));
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (candycrystal.throughWalls.getValue().booleanValue()) {
                    GL11.glEnable((int)2929);
                    GL11.glDepthMask((boolean)true);
                }
            }
            GL11.glEnable((int)3042);
            GL11.glEnable((int)2896);
            GL11.glEnable((int)3553);
            GL11.glEnable((int)3008);
            GL11.glPopAttrib();
            if (candycrystal.glint.getValue().booleanValue()) {
                GL11.glDisable((int)2929);
                GL11.glDepthMask((boolean)false);
                GlStateManager.enableAlpha();
                GlStateManager.color((float)1.0f, (float)0.0f, (float)0.0f, (float)0.13f);
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GlStateManager.disableAlpha();
                GL11.glEnable((int)2929);
                GL11.glDepthMask((boolean)true);
            }
        } else {
            model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        if (candycrystal.isEnable()) {
            GlStateManager.scale((float)(1.0f / candycrystal.scale.getValue().floatValue()), (float)(1.0f / candycrystal.scale.getValue().floatValue()), (float)(1.0f / candycrystal.scale.getValue().floatValue()));

        }
    }
}
