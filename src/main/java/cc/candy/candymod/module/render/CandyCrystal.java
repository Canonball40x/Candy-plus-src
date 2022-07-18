package cc.candy.candymod.module.render;

import cc.candy.candymod.event.events.network.PacketEvent;
import cc.candy.candymod.event.events.render.RenderEntityModelEvent;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CandyCrystal extends Module {
    public Setting<Boolean> chams = this.register(new Setting("Chams", false));
    public Setting<Boolean> throughWalls = this.register(new Setting("ThroughWalls", true));
    public Setting<Boolean> wireframeThroughWalls = this.register(new Setting("WireThroughWalls", true));
    public Setting<Boolean> glint = this.register(new Setting("Glint", false, v -> this.chams.getValue()));
    public Setting<Boolean> wireframe = this.register(new Setting("Wireframe", false));
    public Setting<Float> scale = this.register(new Setting("Scale", 1.0F, 10.0F, 0.1F));
    public Setting<Float> lineWidth = this.register(new Setting("LineWidth", 1.0F, 3.0F, 0.1F));
    public Setting<Boolean> colorSync = this.register(new Setting("Sync", false));
    public Setting<Boolean> rainbow = this.register(new Setting("Rainbow", false));
    public Setting<Integer> saturation = register(new Setting("Saturation", 50, 100, 0, v -> this.rainbow.getValue()));
    public Setting<Integer> brightness = register(new Setting("Brightness", 100, 100, 0, v -> this.rainbow.getValue()));
    public Setting<Integer> speed = register(new Setting("Speed", 40, 100, 1, v -> this.rainbow.getValue()));
    public Setting<Boolean> xqz = register(new Setting<Object>("XQZ", false, v -> !rainbow.getValue() && throughWalls.getValue()));
    public Setting<Color> color = register(new Setting("Color" , new Color(255 , 255 , 255 , 100) , v -> !rainbow.getValue()));
    public Setting<Color> hiddenColor = register(new Setting("Hidden Color" , new Color(255 , 255 , 255 , 100) , v -> xqz.getValue()));
    public Setting<Integer> alpha = register(new Setting("Alpha" , 50 , 255 , 0 , v -> !rainbow.getValue()));
    public Map<EntityEnderCrystal, Float> scaleMap = new ConcurrentHashMap<EntityEnderCrystal, Float>();

    public CandyCrystal()
    {
        super("CandyCrystal" , Categories.RENDER ,false , false);
    }

    @Override
    public void onUpdate() {
		try
		{
        for (Entity crystal : mc.world.loadedEntityList) {
            if (!(crystal instanceof EntityEnderCrystal)) continue;
            if (!this.scaleMap.containsKey(crystal)) {
                this.scaleMap.put((EntityEnderCrystal) crystal, 3.125E-4f);
            } else {
                this.scaleMap.put((EntityEnderCrystal) crystal, this.scaleMap.get(crystal) + 3.125E-4f);
            }
            if (!(this.scaleMap.get(crystal) >= 0.0625f * this.scale.getValue()))
                continue;
            this.scaleMap.remove(crystal);
        }
		}
		catch(Exception e){}
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet = (SPacketDestroyEntities) event.getPacket();
            for (int id : packet.getEntityIDs()) {
                Entity entity = mc.world.getEntityByID(id);
                if (!(entity instanceof EntityEnderCrystal)) continue;
                this.scaleMap.remove(entity);
            }
        }
    }

    @SubscribeEvent
    public void onRenderModel(RenderEntityModelEvent event) {
        if (!(event.entity instanceof EntityEnderCrystal) || !this.wireframe.getValue()) {
            return;
        }
        Color color = this.colorSync.getValue() != false ? getCurrentColor() : this.color.getValue();
        boolean fancyGraphics = mc.gameSettings.fancyGraphics;
        mc.gameSettings.fancyGraphics = false;
        float gamma = mc.gameSettings.gammaSetting;
        mc.gameSettings.gammaSetting = 10000.0f;
        GL11.glPushMatrix();
        GL11.glPushAttrib(1048575);
        GL11.glPolygonMode(1032, 6913);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        if (this.wireframeThroughWalls.getValue().booleanValue()) {
            GL11.glDisable(2929);
        }
        GL11.glEnable(2848);
        GL11.glEnable(3042);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f);
        GlStateManager.glLineWidth(this.lineWidth.getValue().floatValue());
        event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public float hue;
    public Map<Integer, Integer> colorHeightMap = new HashMap<Integer, Integer>();

    @Override
    public void onTick() {
        int colorSpeed = 101 - this.speed.getValue();
        float tempHue = this.hue = (float) (System.currentTimeMillis() % (long) (360 * colorSpeed)) / (360.0f * (float) colorSpeed);
        for (int i = 0; i <= 510; ++i) {
            this.colorHeightMap.put(i, Color.HSBtoRGB(tempHue, (float) this.saturation.getValue().intValue() / 255.0f, (float) this.brightness.getValue().intValue() / 255.0f));
            tempHue += 0.0013071896f;
        }
    }

    public Color getCurrentColor() {
        return Color.getHSBColor(this.hue, (float) this.saturation.getValue().intValue() / 255.0f, (float) this.brightness.getValue().intValue() / 255.0f);
    }

}
