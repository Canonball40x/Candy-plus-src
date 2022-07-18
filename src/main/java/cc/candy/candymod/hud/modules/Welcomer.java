package cc.candy.candymod.hud.modules;

import cc.candy.candymod.hud.Hud;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.RenderUtil;

import java.awt.Color;

public class Welcomer extends Hud {
    public Setting<Boolean> scroll = register(new Setting("Scroll" , false));
    public Setting<Float> speed = register(new Setting("Speed" , 4.0F , 10.0F , 0.1F));

    public Setting<Float> size = new Setting("Size" , 1.0F , 5.0F , 0.5F);
    public Setting<Boolean> shadow = register(new Setting("Shadow" , false));
    public Setting<Color> color = register(new Setting("Color" , new Color(255 , 255 , 255 , 255)));
    public Setting<Boolean> background = register(new Setting("Background" , false));
    public Setting<Color> backcolor = register(new Setting("BGColor" , new Color(40 , 40 , 40 , 60) , v -> background.getValue()));

    private float offsetx = 0.0F;

    public Welcomer(){
        super("Welcomer" , 5 , 5);
    }

    @Override
    public void onRender(){
        String message = "Welcome " + getPlayerName();

        float size = this.size.getValue();
        float width = RenderUtil.getStringWidth(message , size);
        float height = RenderUtil.getStringHeight(size);

        if(background.getValue())
            RenderUtil.drawRect(x.getValue() + offsetx , y.getValue() , width + (20 * size) , height + (10 * size) , ColorUtil.toRGBA(backcolor.getValue()));
        RenderUtil.drawString(message , x.getValue() + 10 + offsetx , y.getValue() + 5 , ColorUtil.toRGBA(color.getValue()) , shadow.getValue() , size);

        if(scroll.getValue()) offsetx += speed.getValue();
        else offsetx = 0;
        if(scaledWidth + RenderUtil.getStringWidth(message , size) + 10 < offsetx) offsetx = RenderUtil.getStringWidth(message , size) * -1 - 10;

        this.width = width + 20 * size;
        this.height = height + (10 * size);
    }

    public String getPlayerName(){
        return mc.player.getName();
    }
}
