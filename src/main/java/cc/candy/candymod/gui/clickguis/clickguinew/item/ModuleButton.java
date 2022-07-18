package cc.candy.candymod.gui.clickguis.clickguinew.item;

import cc.candy.candymod.gui.clickguis.clickguinew.Component;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ModuleButton extends Component {
    public List<Component> componentList = new ArrayList<>();
    public boolean open;

    public ModuleButton(Module module, float x) {
        this.module = module;
        this.x = x;
        this.width = 100;
        open = false;
        this.height = 16;
        module.settings.forEach(this::addSetting);
        add(new BindButton(module , x));
    }

    public float onRender(int mouseX, int mouseY, float x, float y) {
        this.x = x;
        this.y = y;
        RenderUtil.drawRect(x, y, 100, 16, color0);
        if (module.isEnable)
            RenderUtil.drawRect(x, y , 100, 16, ColorUtil.toRGBA(color));
        if (isMouseHovering(mouseX, mouseY))
            RenderUtil.drawRect(x, y, 100, 16, hovering);

        String name = module.name;
        float namey = getCenter(y, 16, RenderUtil.getStringHeight(1.0F));
        RenderUtil.drawString(name, x + 3, namey, ColorUtil.toRGBA(250, 250, 250), false, 1.0F);
        RenderUtil.drawString("...", ((x + 100) - RenderUtil.getStringWidth("...", 1.0F)) - 3, namey - 1, ColorUtil.toRGBA(250, 250, 250), false, 1.0F);
        AtomicReference<Float> height = new AtomicReference<>((float) 16);
        if(open){
            componentList.forEach(c -> {
                float h = c.doRender(mouseX, mouseY, x, y + height.get());
                RenderUtil.drawRect(x, y + height.get(), 2, h, ColorUtil.toRGBA(color));
                height.updateAndGet(v ->  v + h);
            });
        }
        return height.get();
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == MouseUtil.MOUSE_LEFT && isMouseHovering(mouseX , mouseY)){
            this.module.toggle();
        }
        if(mouseButton == MouseUtil.MOUSE_RIGHT && isMouseHovering(mouseX , mouseY)){
            open = !open;
        }

        if(open) componentList.forEach(c -> c.onMouseClicked(mouseX , mouseY , mouseButton));
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int state) {
        if(open) componentList.forEach(c -> c.onMouseReleased(mouseX , mouseY , state));
    }

    public void onMouseClickMove(int mouseX, int mouseY, int clickedMouseButton){
        if(open) componentList.forEach(c -> c.onMouseClickMove(mouseX , mouseY , clickedMouseButton));
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if(open) componentList.forEach(c -> c.onKeyTyped(typedChar , keyCode));
    }

    public void addSetting(Setting setting){
        Object value = setting.value;
        if(value instanceof Boolean) add(new BooleanButton(setting , this.x));
        else if(value instanceof Integer) add(new IntegerSlider(setting , this.x));
        else if(value instanceof Float) add(new FloatSlider(setting , this.x));
        else if(value instanceof Color) add(new ColorSlider(setting , this.x));
        else add(new EnumButton(setting , this.x));
    }

    private void add(Component component){
        componentList.add(component);
    }
}
