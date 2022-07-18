package cc.candy.candymod.gui.clickguis.clickguinew;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.gui.clickguis.clickguinew.item.ModuleButton;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.render.ClickGUI;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;
import cc.candy.candymod.utils.StringUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Panel {
    public float x , y , width , height;
    public boolean open;
    public Color color;
    public Module.Categories category;
    public List<ModuleButton> modules = new ArrayList<>();

    public Panel(float x , float y , Module.Categories category){
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 15;
        this.open = true;
        color = ((ClickGUI)CandyMod.m_module.getModuleWithClass(ClickGUI.class)).color.getValue();
        this.category = category;

        List<Module> modules = new ArrayList<>(CandyMod.m_module.getModulesWithCategories(category));
        modules.sort((c1, c2) -> c1.name.compareToIgnoreCase(c2.name));
        modules.forEach(m -> this.modules.add(new ModuleButton(m , x)));
    }

    public void onRender(int mouseX , int mouseY){
        AtomicReference<Float> _y = new AtomicReference<>(this.y + 15);
        if(open){
            modules.forEach(m -> {
                _y.updateAndGet(v -> v + m.onRender(mouseX, mouseY , this.x , _y.get()));
            });
        }

        String name = StringUtil.getName(category.name());
        RenderUtil.drawRect(x , y , 100 , 15 , ColorUtil.toRGBA(30 , 30 , 30));
        RenderUtil.drawLine(x , y + 15 , x + 100 , y + 15 , 2 , ColorUtil.toRGBA(color));
        float namex = getCenter(x , 100 , RenderUtil.getStringWidth(name , 1.0F));
        float namey = getCenter(y , 15 , RenderUtil.getStringHeight(1.0F));
        RenderUtil.drawString(name , namex , namey , ColorUtil.toRGBA(250 , 250 , 250) , false , 1.0F);

        ClickGUI module = (ClickGUI) CandyMod.m_module.getModuleWithClass(ClickGUI.class);
        if(module == null) return;
        if(module.outline.getValue()){
            //outline
            //x
            RenderUtil.drawLine(x , y , x + 100 , y , 1 , ColorUtil.toRGBA(color));
            RenderUtil.drawLine(x , _y.get() , x + 100 , _y.get() , 1 , ColorUtil.toRGBA(color));
            //y
            RenderUtil.drawLine(x , y , x, _y.get() , 1 , ColorUtil.toRGBA(color));
            RenderUtil.drawLine(x + 100 , y , x + 100 , _y.get() , 1 , ColorUtil.toRGBA(color));
        }
    }

    public boolean moving = false;
    public float diffx , diffy = 0.0F;

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == MouseUtil.MOUSE_LEFT && isMouseHovering(mouseX , mouseY)){
            moving = true;
            diffx = this.x - mouseX;
            diffy = this.y - mouseY;
        }
        if(mouseButton == MouseUtil.MOUSE_RIGHT && isMouseHovering(mouseX , mouseY)){
            open = !open;
        }

        if(open) modules.forEach(m -> m.onMouseClicked(mouseX , mouseY , mouseButton));
    }

    public void onMouseReleased(int mouseX, int mouseY, int state) {
        moving = false;
        if(open) modules.forEach(m -> m.onMouseReleased(mouseX , mouseY , state));
    }

    public void onMouseClickMove(int mouseX, int mouseY, int clickedMouseButton){
        if(clickedMouseButton == MouseUtil.MOUSE_LEFT && moving){
            this.x = mouseX + diffx;
            this.y = mouseY + diffy;
        }
        if(open) modules.forEach(m -> m.onMouseClickMove(mouseX , mouseY , clickedMouseButton));
    }

    public void keyTyped(char typedChar, int keyCode) {
        if(open) modules.forEach(m -> m.onKeyTyped(typedChar , keyCode));
    }

    public Boolean isMouseHovering(int mouseX , int mouseY)
    {
        if(x < mouseX && x + width > mouseX)
        {
            if(y < mouseY && y + height > mouseY)
                return true;
        }
        return false;
    }

    public float getCenter(float a , float b , float c){
        return a + (b - c) / 2;
    }
}
