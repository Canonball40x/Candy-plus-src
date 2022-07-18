package cc.candy.candymod.gui.clickguis.vapegui.components;

import cc.candy.candymod.gui.clickguis.vapegui.Component;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;
import cc.candy.candymod.utils.StringUtil;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ModuleButton extends Component {
    public List<Component> componentList = new ArrayList<>();
    public Module module;
    public boolean open;
    public ModuleButton(Module module , float x){
        this.module = module;
        this.x = x;
        this.width = 110;
        this.height = 18;
        open = false;
        module.settings.forEach(this::addSetting);
    }

    public float doRender(float y , int mouseX , int mouseY){
        this.y = y;

        RenderUtil.drawRect(x , y , width , height , baseColor);
        boolean hovering = isMouseHovering(mouseX , mouseY);
        if(module.isEnable) RenderUtil.drawRect(x , y , width , height - 0.2F , mainColor);
        if(hovering) RenderUtil.drawRect(x , y , width , height , ColorUtil.toRGBA(255 , 255 , 255 , 50));
        float namey = getCenter(y , height , RenderUtil.getStringHeight(1.0F));
        RenderUtil.drawString(module.name , x + 5 , namey , module.isEnable || hovering ? white : gray , false , 1.0F);
        float x = (this.x + width) - RenderUtil.getStringWidth("..." , 1.0F) - 3;
        RenderUtil.drawString("..." , x , namey , module.isEnable || hovering ? white : gray , false , 1.0F);
        AtomicReference<Float> _height = new AtomicReference<>(height);
        if(open) componentList.forEach(c ->{
            float h = c.doRender(_height.get() + this.y , mouseX , mouseY);
            RenderUtil.drawRect(this.x, y + _height.get(), 2, h, mainColor);
            _height.updateAndGet(v -> v + h);
        });
        return _height.get();
    }

    @Override
    public void onMouseClicked(int mouseX , int mouseY , int clickedMouseButton){
        if(isMouseHovering(mouseX , mouseY) && clickedMouseButton == MouseUtil.MOUSE_RIGHT) open = !open;
        if(open) componentList.forEach(c -> c.onMouseClicked(mouseX , mouseY , clickedMouseButton));
    }

    public void addSetting(Setting stg){
        if(stg.getValue() instanceof Boolean) componentList.add(new BooleanButton(stg , this.x));
        if(stg.getValue() instanceof Enum) componentList.add(new EnumButton(stg , this.x));
    }
}
