package cc.candy.candymod.gui.clickguis.vapegui.components;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.gui.clickguis.vapegui.Component;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;

public class BooleanButton extends Component {
    public Setting<Boolean> setting;
    public float _x = 0;

    public BooleanButton(Setting setting , float x){
        this.setting = setting;
        this.x = x;
        this.width = 110;
        this.height = 18;
        _x = getX();
    }

    @Override
    public float doRender(float y , int mouseX , int mouseY){
        this.y = y;
        RenderUtil.drawRect(x , y , width , height , baseColor);
        float namey = getCenter(y , height , RenderUtil.getStringHeight(1.0F));
        RenderUtil.drawString(setting.name , x + 8 , namey , setting.getValue() ? white : gray , false , 1.0F);
        float linex = (this.x + this.width) - 20;
        RenderUtil.drawRect(linex , namey - 3 , 15 , 8 , setting.getValue() ? mainColor : ColorUtil.toRGBA(60,60,60));
        RenderUtil.drawRect(_x , namey - 2 , 4 , 6 , baseColor);
        if(isMouseHovering(mouseX , mouseY , _x , namey - 2 , 4 , 6))  RenderUtil.drawRect(_x , namey - 2 , 4 , 6 , ColorUtil.toRGBA(255 , 255 , 255 , 50));
        _x += (getX() - _x) * 0.5;
        return height;
    }

    @Override
    public void onMouseClicked(int mouseX , int mouseY , int clickedMouseButton){
        float namey = getCenter(y , height , RenderUtil.getStringHeight(1.0F));
        if(isMouseHovering(mouseX , mouseY , _x , namey - 2 , 4 , 6) && MouseUtil.MOUSE_LEFT == clickedMouseButton) setting.setValue(!setting.getValue());
    }

    public float getX(){
        return this.setting.getValue() ? (this.x + this.width) - 13 : (this.x + this.width) - 20;
    }
}
