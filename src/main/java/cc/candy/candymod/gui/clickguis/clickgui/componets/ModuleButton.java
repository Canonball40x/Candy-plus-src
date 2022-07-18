package cc.candy.candymod.gui.clickguis.clickgui.componets;

import cc.candy.candymod.gui.clickguis.clickgui.Component;
import cc.candy.candymod.gui.clickguis.clickgui.Panel;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.render.HUDEditor;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleButton extends Component {

    public List<Component> components = new ArrayList<>();
    public Module module;
    private boolean isOpening = false;

    public ModuleButton(Module module, int x, int width , int height)
    {
        this.module = module;
        this.x = x;
        this.width = width;
        this.height = height;

        //setting buttons
        module.settings
                .forEach(s -> addButtonBySetting(s));

        if(module.getClass() != HUDEditor.class)
            components.add(new KeybindButton(module , x , width , height));
    }

    public void onRender(int y)
    {
        //for move
        this.y = y;
        //draw rect
        RenderUtil.drawRect(x , y , width , height , defaultColor);
        if(module.isEnable)
            RenderUtil.drawRect(x, y, width, height , enabledColor);

        float centeredY = y + (this.height - RenderUtil.getStringHeight(1.0F)) / 2;
        RenderUtil.drawString(module.name , x + 3  ,centeredY ,module.isEnable ? ColorUtil.toRGBA(255 , 255 , 255 , 255) :  ColorUtil.toARGB(255 , 255 , 255 , 255) , false ,1.0F);

        //draw setting
        if(isOpening) {
            components.forEach(c -> {
                Panel.Cy += height;
                c.onRender2D(Panel.Cy);
                if(!c.visible)
                {
                    Panel.Cy -= height;
                }
            });
        }

        //RenderUtil.drawLine(x , y , x + width , y , 2 , outlineColor);
    }

    public void addButtonBySetting(Setting s)
    {
        //boolean button
        if(s.getValue() instanceof Boolean)
            components.add(new BooleanButton(module , s , x , width , height ));

        else if(s.getValue() instanceof Integer)
            components.add(new SliderButton(module , s , x , width , height ));

        else if(s.getValue() instanceof Float)
            components.add(new FloatSliderButton(module , s , x , width , height));

        else if(s.getValue() instanceof Color)
            components.add(new ColorButton(module , s , x , width , height ));

        else
            components.add(new EnumButton(module , s , x , width , height));
    }

    public void changeX(int x)
    {
        this.x = x;
        components.forEach(c -> c.x = x);
    }

    //events
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        //send event
        if(isOpening)
            components.forEach(c ->
            {
                if(c.visible)
                    c.mouseClicked(mouseX , mouseY , mouseButton);
            });


        //toggle
        if(isMouseHovering(mouseX , mouseY) && mouseButton == MouseUtil.MOUSE_LEFT)
            module.toggle();

        //open menu
        if(isMouseHovering(mouseX , mouseY) && mouseButton == MouseUtil.MOUSE_RIGHT)
            isOpening = !isOpening;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        //send event
        if(isOpening)
            components.forEach(c -> c.mouseReleased(mouseX , mouseY , state));
    }

    @Override
    public void mouseClickMove(int mouseX , int mouseY , int clickedMouseButton)
    {
        //send event
        if(isOpening)
            components.forEach(c -> c.mouseClickMove(mouseX , mouseY , clickedMouseButton));
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode)
    {
        if(isOpening)
            components.forEach(c -> c.onKeyTyped(typedChar , keyCode));
    }
}
