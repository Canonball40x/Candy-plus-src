package cc.candy.candymod.gui.clickguis.clickguinew.item;

import cc.candy.candymod.gui.clickguis.clickguinew.Component;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;

public class IntegerSlider extends Component {
    public Setting<Integer> setting;
    public float ratio;
    public boolean changing;

    public IntegerSlider(Setting<Integer> setting , float x){
        this.setting = setting;
        this.x = x;
        this.width = 100;
        this.height = 16;
        this.ratio = getRatio(setting.getValue() , setting.maxValue , setting.minValue);
        changing = false;
    }

    @Override
    public float doRender(int mouseX , int mouseY , float x , float y){
        if(setting.visible()){
            this.x = x;
            this.y = y;
            RenderUtil.drawRect(x, y, 100, 16, color0);
            float width = this.width * ratio;
            RenderUtil.drawRect(x , y, width , 16 , ColorUtil.toRGBA(color));
            float fonty = this.y + (this.height - RenderUtil.getStringHeight(1.0F)) / 2;
            RenderUtil.drawString(setting.name , x + 6 , fonty , ColorUtil.toRGBA(250, 250, 250) , false , 1.0F);
            RenderUtil.drawString(setting.getValue().toString() , (this.x + this.width) - RenderUtil.getStringWidth(setting.getValue().toString() , 1.0F) - 6
                    , fonty , ColorUtil.toRGBA(250, 250, 250) , false , 1.0F);

            return 16.0F;
        }

        return 0.0F;
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isMouseHovering(mouseX , mouseY) && mouseButton == MouseUtil.MOUSE_LEFT){
            setValue(mouseX);
            changing = true;
        }
    }

    @Override
    public void onMouseClickMove(int mouseX, int mouseY, int clickedMouseButton){
        if(changing && clickedMouseButton == MouseUtil.MOUSE_LEFT) setValue(mouseX);
    }

    @Override
    public void onMouseReleased(int mouseX , int mouseY , int state){
        changing = false;
    }

    public void setValue(float mouseX) {
        float v = (mouseX - this.x) / (width);
        if(v > 1.0F) v = 1.0F;
        if(v < 0.0F) v = 0.0F;
        this.ratio = v;
        float newValue = ((setting.maxValue - setting.minValue) * ratio) + setting.minValue;
        setting.setValue(Math.round(newValue));
    }

    public float getRatio(float value , float maxValue , float minValue){
        return ((value - minValue) / maxValue) ;
    }
}
