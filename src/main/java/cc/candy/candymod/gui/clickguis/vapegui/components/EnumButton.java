package cc.candy.candymod.gui.clickguis.vapegui.components;

import cc.candy.candymod.gui.clickguis.vapegui.Component;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.RenderUtil;

public class EnumButton extends Component {
    public Setting<Enum> setting;
    public boolean open;

    public EnumButton(Setting<Enum> setting , float x){
        this.setting = setting;
        this.x = x;
        this.width = 110;
        this.height = 18;
        open = false;
    }

    @Override
    public float doRender(float y , int mouseX , int mouseY){
        this.y = y;
        float width = RenderUtil.getStringWidth(setting.getValue().name() , 1.0F);
        float namey = getCenter(y , height , RenderUtil.getStringHeight(1.0F));
        RenderUtil.drawRect(this.x , this.y , width , height , baseColor);
        RenderUtil.drawString(setting.name , x + 8 , namey , white , false , 1.0F);
        RenderUtil.drawString(setting.getValue().name() , (x + this.width) - width - 5 , namey , white , false , 1.0F);
        return height;
    }
}
