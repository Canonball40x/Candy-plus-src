package cc.candy.candymod.hud.modules;

import cc.candy.candymod.hud.Hud;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.CandyDynamicTexture;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Watermark extends Hud {
    public Setting<Float> scale = register(new Setting("Scale" , 0.6F , 1.0F , 0.1F));
    public Setting<Boolean> rainbow = this.register(new Setting("Rainbow", false));
    public Setting<Integer> saturation = register(new Setting("Saturation", 50, 100, 0, v -> this.rainbow.getValue()));
    public Setting<Integer> brightness = register(new Setting("Brightness", 100, 100, 0, v -> this.rainbow.getValue()));
    public Setting<Integer> speed = register(new Setting("Speed", 40, 100, 1, v -> this.rainbow.getValue()));

    public CandyDynamicTexture watermark = null;

    public Watermark(){
        super("Watermark" , 10 , 10);
        loadLogo();
    }

    @Override
    public void onRender(){
        if(watermark == null) {
            loadLogo();
            return;
        }

        float width = watermark.GetWidth() * scale.getValue();
        float height = watermark.GetHeight() * scale.getValue();

        GlStateManager.enableTexture2D();
        GlStateManager.disableAlpha();
        RenderHelper.enableGUIStandardItemLighting();
        mc.renderEngine.bindTexture(watermark.GetResourceLocation());
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        if(rainbow.getValue()){
            Color color = new Color(RenderUtil.getRainbow(speed.getValue() * 100, 0, (float) saturation.getValue().intValue() / 100.0f, (float) brightness.getValue().intValue() / 100.0f));
            GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), 1.0F);
        }
        else{
            GlStateManager.color( 1.0F, 1.0F, 1.0F, 1.0F);
        }
        GlStateManager.pushMatrix();
        RenderUtil.drawTexture(this.x.getValue() , this.y.getValue() , width , height);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();

        this.width = width;
        this.height = height;
    }

    public void loadLogo(){
        InputStream stream = Watermark.class.getResourceAsStream("/assets/candy/watermark.png");
        BufferedImage image;
        try {
            image = ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int height = image.getHeight();
        int width = image.getWidth();
        watermark = new CandyDynamicTexture(image, height, width);

        watermark.SetResourceLocation(Minecraft.getMinecraft().getTextureManager()
                .getDynamicTextureLocation("candy/textures", watermark));
    }
}
