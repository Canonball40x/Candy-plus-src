package cc.candy.candymod.hud;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.render.HUDEditor;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class Hud extends Module {
    public Setting<Float> x , y;
    public float width , height = 0.0F;
    public boolean selecting = false;

    public Hud(String name , float x , float y){
        super(name , Categories.HUB , false , false);
        this.x = register(new Setting("X" , x , 2000.0F , 0.0F));
        this.y = register(new Setting("Y" , x , 2000.0F , 0.0F));
    }

    @Override
    public void onRender2D(){
        //draw for hubeditor
        if(CandyMod.m_module.getModuleWithClass(HUDEditor.class).isEnable) {
            Color color = selecting ? new Color(20, 20, 20, 110) : new Color(20, 20, 20, 80);
            RenderUtil.drawRect(x.getValue() - 10, y.getValue() - 5
                    , width + 20, height + 10, ColorUtil.toRGBA(color));
        }

        onRender();
    }

    public void onRender(){

    }

    @Override
    public void onUpdate(){
        updateResolution();
        this.x.maxValue = scaledWidth;
        this.y.maxValue = scaledHeight;
    }

    private float diffX , diffY = 0.0F;
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == MouseUtil.MOUSE_LEFT && isMouseHovering(mouseX , mouseY)){
            diffX = x.getValue() - mouseX;
            diffY = y.getValue() - mouseY;
            selecting = true;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        selecting = false;
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if(selecting && clickedMouseButton == MouseUtil.MOUSE_LEFT){
            x.setValue(mouseX + diffX);
            y.setValue(mouseY + diffY);
        }
    }

    public Boolean isMouseHovering(int mouseX , int mouseY)
    {
        if(x.getValue() - 10 < mouseX && x.getValue() + width + 10 > mouseX)
        {
            if(y.getValue() - 10 < mouseY && y.getValue() + height + 10 > mouseY)
                return true;
        }

        return false;
    }

    public void setWidth(float width){
        this.width = width;
    }

    public void setHeight(float height){
        this.height = height;
    }

    public float scaledWidth , scaledHeight , scaleFactor = 0.0F;
    public void updateResolution() {
        this.scaledWidth = mc.displayWidth;
        this.scaledHeight = mc.displayHeight;
        this.scaleFactor = 1;
        boolean flag = mc.isUnicode();
        int i = mc.gameSettings.guiScale;
        if (i == 0) {
            i = 1000;
        }
        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }
        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }
        double scaledWidthD = (double) this.scaledWidth / (double) this.scaleFactor;
        double scaledHeightD = (double) this.scaledHeight / (double) this.scaleFactor;
        this.scaledWidth = MathHelper.ceil(scaledWidthD);
        this.scaledHeight = MathHelper.ceil(scaledHeightD);
    }
}
